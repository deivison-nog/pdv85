<?php
declare(strict_types=1);

require_once __DIR__ . '/../app/bootstrap.php';
require_once __DIR__ . '/../app/auth.php';
require_once __DIR__ . '/../app/helpers.php';
require_once __DIR__ . '/../app/db.php';

bootstrap_app();
require_auth();

$pdo = db();
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
  $limit = (int)($_GET['limit'] ?? 100);
  $limit = max(1, min(300, $limit));

  $stmt = $pdo->prepare("
    SELECT id, total, discount_total, payment_method, cash_paid, cash_change, status, created_at
    FROM sales
    ORDER BY id DESC
    LIMIT :l
  ");
  $stmt->bindValue(':l', $limit, PDO::PARAM_INT);
  $stmt->execute();
  json_response(['ok' => true, 'data' => $stmt->fetchAll()]);
}

$body = post_json();

if ($method === 'POST' && (($body['action'] ?? '') === 'cancel')) {
  $id = (int)($body['id'] ?? 0);
  if ($id <= 0) json_response(['ok' => false, 'error' => 'ID inválido'], 422);

  $stmt = $pdo->prepare("UPDATE sales SET status='CANCELADA' WHERE id=:id");
  $stmt->execute([':id' => $id]);
  json_response(['ok' => true]);
}

if ($method === 'POST') {
  $payment = (string)($body['payment_method'] ?? 'PIX');
  $discount = max(0.0, ffloat($body['discount_total'] ?? 0));
  $cashPaid = isset($body['cash_paid']) ? ffloat($body['cash_paid']) : null;
  $cashChange = isset($body['cash_change']) ? ffloat($body['cash_change']) : null;

  $items = $body['items'] ?? [];
  if (!in_array($payment, ['DINHEIRO','PIX','DEBITO','CREDITO'], true)) {
    json_response(['ok' => false, 'error' => 'Pagamento inválido'], 422);
  }
  if (!is_array($items) || count($items) === 0) {
    json_response(['ok' => false, 'error' => 'Carrinho vazio'], 422);
  }

  $pdo->beginTransaction();
  try {
    $lines = [];
    $subtotal = 0.0;

    foreach ($items as $it) {
      $pid = (int)($it['product_id'] ?? 0);
      $qty = (int)($it['qty'] ?? 0);
      if ($pid <= 0 || $qty <= 0) throw new RuntimeException('Item inválido');

      $p = $pdo->prepare("SELECT id, cost_price, price, stock FROM products WHERE id=:id");
      $p->execute([':id' => $pid]);
      $prod = $p->fetch();
      if (!$prod) throw new RuntimeException("Produto não encontrado: $pid");
      if ((int)$prod['stock'] < $qty) throw new RuntimeException("Estoque insuficiente (produto $pid)");

      $price = (float)$prod['price'];
      $cost  = (float)$prod['cost_price'];
      $lineTotal = $price * $qty;
      $subtotal += $lineTotal;

      $lines[] = [
        'product_id' => $pid,
        'qty' => $qty,
        'price' => $price,
        'cost_price' => $cost,
        'line_total' => $lineTotal,
      ];
    }

    $discount = min($discount, $subtotal);
    $total = max(0.0, $subtotal - $discount);

    // valida dinheiro
    if ($payment === 'DINHEIRO') {
      if ($cashPaid === null) throw new RuntimeException('Informe o valor pago no dinheiro.');
      if ($cashPaid < $total) throw new RuntimeException('Valor pago é menor que o total.');
      $cashChange = $cashPaid - $total;
    } else {
      $cashPaid = null;
      $cashChange = null;
    }

    $s = $pdo->prepare("
      INSERT INTO sales (total, discount_total, payment_method, cash_paid, cash_change, status)
      VALUES (:t,:d,:p,:cp,:cc,'OK')
    ");
    $s->execute([':t'=>$total, ':d'=>$discount, ':p'=>$payment, ':cp'=>$cashPaid, ':cc'=>$cashChange]);
    $saleId = (int)$pdo->lastInsertId();

    $insItem = $pdo->prepare("
      INSERT INTO sale_items (sale_id, product_id, qty, cost_price, price, line_total)
      VALUES (:sid,:pid,:q,:c,:pr,:lt)
    ");
    $updStock = $pdo->prepare("UPDATE products SET stock = stock - :q WHERE id = :id");

    foreach ($lines as $ln) {
      $insItem->execute([
        ':sid' => $saleId,
        ':pid' => $ln['product_id'],
        ':q'   => $ln['qty'],
        ':c'   => $ln['cost_price'],
        ':pr'  => $ln['price'],
        ':lt'  => $ln['line_total'],
      ]);
      $updStock->execute([':q' => $ln['qty'], ':id' => $ln['product_id']]);
    }

    $pdo->commit();
    json_response(['ok' => true, 'sale_id' => $saleId, 'total' => $total, 'cash_change' => $cashChange]);
  } catch (Throwable $e) {
    $pdo->rollBack();
    json_response(['ok' => false, 'error' => $e->getMessage()], 500);
  }
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);