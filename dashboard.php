<?php
declare(strict_types=1);

require_once __DIR__ . '/app/db.php';
$cfg = require __DIR__ . '/app/config.php';
$pdo = db();

$active = 'dashboard';
$title = 'Dashboard • PDV Info85';
$header = 'Dashboard';
$subheader = 'Boas-vindas • estatísticas';

$today = (float)$pdo->query("SELECT IFNULL(SUM(total),0) v FROM sales WHERE status='OK' AND DATE(created_at)=CURDATE()")->fetchColumn();
$month = (float)$pdo->query("SELECT IFNULL(SUM(total),0) v FROM sales WHERE status='OK' AND YEAR(created_at)=YEAR(CURDATE()) AND MONTH(created_at)=MONTH(CURDATE())")->fetchColumn();
$totalProducts = (int)$pdo->query("SELECT COUNT(*) c FROM products")->fetchColumn();
$lowStock = (int)$cfg['low_stock_threshold'];
$lowStockCount = (int)$pdo->query("SELECT COUNT(*) c FROM products WHERE stock <= {$lowStock}")->fetchColumn();

ob_start();
?>
<div class="row g-3">
  <div class="col-12 col-md-6 col-xl-3"><div class="card card-app"><div class="card-body">
    <div class="text-secondary small">Vendas hoje</div><div class="h4 fw-bold mb-0">R$ <?= number_format($today,2,',','.') ?></div>
  </div></div></div>
  <div class="col-12 col-md-6 col-xl-3"><div class="card card-app"><div class="card-body">
    <div class="text-secondary small">Vendas do mês</div><div class="h4 fw-bold mb-0">R$ <?= number_format($month,2,',','.') ?></div>
  </div></div></div>
  <div class="col-12 col-md-6 col-xl-3"><div class="card card-app"><div class="card-body">
    <div class="text-secondary small">Total produtos</div><div class="h4 fw-bold mb-0"><?= $totalProducts ?></div>
  </div></div></div>
  <div class="col-12 col-md-6 col-xl-3"><div class="card card-app"><div class="card-body">
    <div class="text-secondary small">Estoque baixo (≤ <?= $lowStock ?>)</div><div class="h4 fw-bold mb-0"><?= $lowStockCount ?></div>
  </div></div></div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';