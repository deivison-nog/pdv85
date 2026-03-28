<?php
declare(strict_types=1);

require_once __DIR__ . '/../app/bootstrap.php';
require_once __DIR__ . '/../app/auth.php';
require_once __DIR__ . '/../app/helpers.php';
require_once __DIR__ . '/../app/db.php';

bootstrap_app();
require_auth();

$pdo = db();

$from = (string)($_GET['from'] ?? date('Y-m-d'));
$to   = (string)($_GET['to'] ?? date('Y-m-d'));

$fromDT = $from . " 00:00:00";
$toDT   = $to   . " 23:59:59";

$sales = $pdo->prepare("
  SELECT id, total, discount_total
  FROM sales
  WHERE status='OK' AND created_at BETWEEN :f AND :t
");
$sales->execute([':f'=>$fromDT, ':t'=>$toDT]);
$salesRows = $sales->fetchAll();

$totalSales = 0.0;
$totalDiscount = 0.0;
$totalProfit = 0.0;

$itemsStmt = $pdo->prepare("
  SELECT qty, cost_price, price, line_total
  FROM sale_items
  WHERE sale_id = :sid
");

foreach ($salesRows as $s) {
  $saleId = (int)$s['id'];
  $totalSales += (float)$s['total'];
  $totalDiscount += (float)$s['discount_total'];

  $itemsStmt->execute([':sid'=>$saleId]);
  $items = $itemsStmt->fetchAll();

  $subtotal = 0.0;
  foreach ($items as $it) $subtotal += (float)$it['line_total'];

  $discount = (float)$s['discount_total'];

  // Lucro líquido: rateia desconto proporcional ao line_total de cada item
  foreach ($items as $it) {
    $line = (float)$it['line_total'];
    $share = ($subtotal > 0) ? ($line / $subtotal) : 0;
    $lineDiscount = $discount * $share;

    $rev = $line - $lineDiscount;
    $cost = (float)$it['cost_price'] * (int)$it['qty'];

    $totalProfit += ($rev - $cost);
  }
}

json_response([
  'ok' => true,
  'data' => [
    'from' => $from,
    'to' => $to,
    'total_sales' => $totalSales,
    'total_discount' => $totalDiscount,
    'profit_net' => $totalProfit,
  ]
]);