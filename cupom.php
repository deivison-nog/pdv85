<?php
declare(strict_types=1);

require_once __DIR__ . '/app/bootstrap.php';
require_once __DIR__ . '/app/auth.php';
require_once __DIR__ . '/app/db.php';
require_once __DIR__ . '/app/helpers.php';

bootstrap_app();
require_auth();

$pdo = db();
$saleId = (int)($_GET['sale_id'] ?? 0);

$settings = [];
foreach ($pdo->query("SELECT `key`,`value` FROM settings")->fetchAll() as $r) $settings[$r['key']] = $r['value'];

$company = $settings['company_name'] ?? 'Minha Empresa';
$width = (int)($settings['coupon_width_mm'] ?? 58);

$sale = null;
$items = [];

if ($saleId > 0) {
  $st = $pdo->prepare("SELECT * FROM sales WHERE id=:id");
  $st->execute([':id' => $saleId]);
  $sale = $st->fetch();

  $it = $pdo->prepare("
    SELECT si.qty, si.price, si.line_total, p.name, p.upc
    FROM sale_items si
    JOIN products p ON p.id = si.product_id
    WHERE si.sale_id = :id
  ");
  $it->execute([':id' => $saleId]);
  $items = $it->fetchAll();
}
?>
<!doctype html>
<html lang="pt-br">
<head>
  <meta charset="utf-8">
  <title>Cupom #<?= (int)$saleId ?></title>
  <style>
    body{font-family: monospace; margin:0; padding:0;}
    .cupom{width: <?= $width ?>mm; padding: 6mm;}
    .center{text-align:center}
    .row{display:flex; justify-content:space-between}
    hr{border:none; border-top:1px dashed #000; margin:6px 0}
    @media print { .no-print{display:none} }
  </style>
</head>
<body>
  <div class="cupom">
    <div class="center">
      <div><b><?= escape($company) ?></b></div>
      <div>CUPOM NÃO FISCAL</div>
    </div>
    <hr>
    <div class="row"><span>Venda:</span><span>#<?= (int)$saleId ?></span></div>
    <?php if ($sale): ?>
      <div class="row"><span>Data:</span><span><?= escape((string)$sale['created_at']) ?></span></div>
      <div class="row"><span>Pag:</span><span><?= escape((string)$sale['payment_method']) ?></span></div>
      <div class="row"><span>Status:</span><span><?= escape((string)$sale['status']) ?></span></div>
      <?php if ($sale['payment_method'] === 'DINHEIRO'): ?>
        <div class="row"><span>Pago:</span><span><?= number_format((float)$sale['cash_paid'],2,',','.') ?></span></div>
        <div class="row"><span>Troco:</span><span><?= number_format((float)$sale['cash_change'],2,',','.') ?></span></div>
      <?php endif; ?>
    <?php endif; ?>
    <hr>

    <?php foreach ($items as $i): ?>
      <div><?= escape((string)$i['name']) ?></div>
      <div class="row">
        <span><?= (int)$i['qty'] ?> x <?= number_format((float)$i['price'],2,',','.') ?></span>
        <span><?= number_format((float)$i['line_total'],2,',','.') ?></span>
      </div>
    <?php endforeach; ?>

    <hr>
    <?php if ($sale): ?>
      <div class="row"><span>Desconto:</span><span><?= number_format((float)$sale['discount_total'],2,',','.') ?></span></div>
      <div class="row"><span><b>Total:</b></span><span><b><?= number_format((float)$sale['total'],2,',','.') ?></b></span></div>
    <?php endif; ?>

    <hr>
    <div class="center">Obrigado!</div>
  </div>
</body>
</html>