<?php
declare(strict_types=1);

require_once __DIR__ . '/../app/bootstrap.php';
require_once __DIR__ . '/../app/auth.php';
require_once __DIR__ . '/../app/helpers.php';
require_once __DIR__ . '/../app/db.php';

bootstrap_app();
require_auth();

$pdo = db();
$cfg = require __DIR__ . '/../app/config.php';

$today  = (float)$pdo->query("SELECT IFNULL(SUM(total),0) v FROM sales WHERE status='OK' AND DATE(created_at)=CURDATE()")->fetchColumn();
$month  = (float)$pdo->query("SELECT IFNULL(SUM(total),0) v FROM sales WHERE status='OK' AND YEAR(created_at)=YEAR(CURDATE()) AND MONTH(created_at)=MONTH(CURDATE())")->fetchColumn();
$totalProducts  = (int)$pdo->query("SELECT COUNT(*) c FROM products")->fetchColumn();
$lowStock       = (int)$cfg['low_stock_threshold'];
$stmtLow = $pdo->prepare("SELECT COUNT(*) c FROM products WHERE stock <= :threshold");
$stmtLow->execute([':threshold' => $lowStock]);
$lowStockCount  = (int)$stmtLow->fetchColumn();

json_response([
  'ok' => true,
  'data' => [
    'sales_today'         => $today,
    'sales_month'         => $month,
    'total_products'      => $totalProducts,
    'low_stock_threshold' => $lowStock,
    'low_stock_count'     => $lowStockCount,
  ],
]);
