<?php
declare(strict_types=1);

require_once __DIR__ . '/db.php';

function bootstrap_app(): void {
  seed_admin_user();
  seed_default_settings();
  seed_default_categories();
}

function seed_admin_user(): void {
  $cfg = require __DIR__ . '/config.php';
  $admin = $cfg['seed_admin'];

  $pdo = db();
  $stmt = $pdo->prepare("SELECT id FROM users WHERE username = :u LIMIT 1");
  $stmt->execute([':u' => $admin['username']]);
  if ($stmt->fetchColumn()) return;

  $hash = password_hash($admin['password'], PASSWORD_DEFAULT);
  $ins = $pdo->prepare("INSERT INTO users (username, password_hash, role) VALUES (:u, :h, :r)");
  $ins->execute([':u' => $admin['username'], ':h' => $hash, ':r' => $admin['role']]);
}

function seed_default_settings(): void {
  $pdo = db();
  $defaults = [
    'company_name' => 'Minha Empresa',
    'company_cnpj' => '',
    'coupon_width_mm' => '58',
    'coupon_copies' => '2',
    'coupon_auto_print' => '1',
    'theme' => 'dark', // dark|light
  ];

  $sel = $pdo->prepare("SELECT `value` FROM settings WHERE `key` = :k");
  $ins = $pdo->prepare("INSERT INTO settings (`key`, `value`) VALUES (:k, :v)");

  foreach ($defaults as $k => $v) {
    $sel->execute([':k' => $k]);
    if ($sel->fetchColumn() === false) {
      $ins->execute([':k' => $k, ':v' => $v]);
    }
  }
}

function seed_default_categories(): void {
  $pdo = db();

  $count = (int)$pdo->query("SELECT COUNT(*) c FROM categories")->fetch()['c'];
  if ($count > 0) return;

  $cats = ['Geral', 'Bebidas', 'Mercearia', 'Higiene', 'Limpeza'];
  $ins = $pdo->prepare("INSERT INTO categories (name) VALUES (:n)");
  foreach ($cats as $c) $ins->execute([':n' => $c]);
}