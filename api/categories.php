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
  $rows = $pdo->query("SELECT id, name FROM categories ORDER BY name ASC")->fetchAll();
  json_response(['ok' => true, 'data' => $rows]);
}

$body = post_json();

if ($method === 'POST') {
  $name = trim((string)($body['name'] ?? ''));
  if ($name === '') json_response(['ok' => false, 'error' => 'Nome obrigatório'], 422);

  $stmt = $pdo->prepare("INSERT INTO categories (name) VALUES (:n)");
  $stmt->execute([':n' => $name]);
  json_response(['ok' => true, 'id' => (int)$pdo->lastInsertId()]);
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);