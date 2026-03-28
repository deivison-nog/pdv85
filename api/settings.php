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
  $rows = $pdo->query("SELECT `key`, `value` FROM settings")->fetchAll();
  $data = [];
  foreach ($rows as $r) $data[$r['key']] = $r['value'];
  json_response(['ok' => true, 'data' => $data]);
}

if ($method === 'POST') {
  $body = post_json();
  $allowed = ['company_name','company_cnpj','coupon_copies','coupon_auto_print','coupon_width_mm','theme'];

  $ins = $pdo->prepare("
    INSERT INTO settings (`key`,`value`) VALUES (:k,:v)
    ON DUPLICATE KEY UPDATE `value`=VALUES(`value`)
  ");

  foreach ($allowed as $k) {
    if (array_key_exists($k, $body)) {
      $ins->execute([':k' => $k, ':v' => (string)$body[$k]]);
    }
  }
  json_response(['ok' => true]);
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);