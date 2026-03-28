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

/**
 * Retorna:
 * - total_sales (soma sales.total)
 * - total_discount (soma sales.discount_total)
 * - profit_net (lucro líquido com desconto rateado)
 * - series: [{date, sales_total, profit_net}]
 * - top_products: [{product_id, name, upc, profit_net}]
 */

$salesStmt = $pdo->prepare("
  SELECT id, total, discount_total, DATE(created_at) AS d
  FROM sales
  WHERE status='OK' AND created_at BETWEEN :f AND :t
  ORDER BY created_at ASC
");
$salesStmt->execute([':f'=>$fromDT, ':t'=>$toDT]);
$salesRows = $salesStmt->fetchAll();

$itemsStmt = $pdo->prepare("
  SELECT si.qty, si.cost_price, si.price, si.line_total, p.name, p.upc, p.id AS product_id
  FROM sale_items si
  JOIN products p ON p.id = si.product_id
  WHERE si.sale_id = :sid
");

$totalSales = 0.0;
$totalDiscount = 0.0;
$totalProfit = 0.0;

$seriesMap = [];     // date => ['sales_total'=>, 'profit_net'=>]
$profitByProduct = []; // product_id => ['name','upc','profit_net']

foreach ($salesRows as $s) {
  $saleId = (int)$s['id'];
  $date = (string)$s['d'];

  $saleTotal = (float)$s['total'];
  $saleDiscount = (float)$s['discount_total'];

  $totalSales += $saleTotal;
  $totalDiscount += $saleDiscount;

  if (!isset($seriesMap[$date])) $seriesMap[$date] = ['sales_total' => 0.0, 'profit_net' => 0.0];
  $seriesMap[$date]['sales_total'] += $saleTotal;

  $itemsStmt->execute([':sid'=>$saleId]);
  $items = $itemsStmt->fetchAll();

  $subtotal = 0.0;
  foreach ($items as $it) $subtotal += (float)$it['line_total'];

  foreach ($items as $it) {
    $line = (float)$it['line_total'];
    $share = ($subtotal > 0) ? ($line / $subtotal) : 0.0;
    $lineDiscount = $saleDiscount * $share;

    $rev = $line - $lineDiscount;
    $cost = (float)$it['cost_price'] * (int)$it['qty';
    $profit = $rev - $cost;

    $totalProfit += $profit;
    $seriesMap[$date]['profit_net'] += $profit;

    $pid = (int)$it['product_id'];
    if (!isset($profitByProduct[$pid])) {
      $profitByProduct[$pid] = [
        'product_id' => $pid,
        'name' => (string)$it['name'],
        'upc' => (string)($it['upc'] ?? ''),
        'profit_net' => 0.0,
      ];
    }
    $profitByProduct[$pid]['profit_net'] += $profit;
  }
}

$series = [];
ksort($seriesMap);
foreach ($seriesMap as $d => $v) {
  $series[] = [
    'date' => $d,
    'sales_total' => $v['sales_total'],
    'profit_net' => $v['profit_net'],
  ];
}

$topProducts = array_values($profitByProduct);
usort($topProducts, fn($a,$b) => ($b['profit_net'] <=> $a['profit_net']));
$topProducts = array_slice($topProducts, 0, 10);

json_response([
  'ok' => true,
  'data' => [
    'from' => $from,
    'to' => $to,
    'total_sales' => $totalSales,
    'total_discount' => $totalDiscount,
    'profit_net' => $totalProfit,
    'series' => $series,
    'top_products' => $topProducts,
  ]
]);