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
  $q = trim((string)($_GET['q'] ?? ''));
  if ($q === '') {
    $rows = $pdo->query("SELECT id, name, address, debt_to_supplier FROM suppliers ORDER BY name ASC")->fetchAll();
    json_response(['ok' => true, 'data' => $rows]);
  }
  $st = $pdo->prepare("SELECT id, name, address, debt_to_supplier FROM suppliers WHERE name LIKE :q ORDER BY name ASC");
  $st->execute([':q' => "%$q%"]);
  json_response(['ok' => true, 'data' => $st->fetchAll()]);
}

$body = post_json();

if ($method === 'POST') {
  $name = trim((string)($body['name'] ?? ''));
  $address = trim((string)($body['address'] ?? ''));
  $debt = ffloat($body['debt_to_supplier'] ?? 0);

  if ($name === '') json_response(['ok' => false, 'error' => 'Nome é obrigatório.'], 422);

  $st = $pdo->prepare("INSERT INTO suppliers (name, address, debt_to_supplier) VALUES (:n,:a,:d)");
  $st->execute([':n' => $name, ':a' => ($address===''?null:$address), ':d' => $debt]);
  json_response(['ok' => true, 'id' => (int)$pdo->lastInsertId()]);
}

if ($method === 'PUT') {
  $id = (int)($body['id'] ?? 0);
  if ($id <= 0) json_response(['ok' => false, 'error' => 'ID inválido.'], 422);

  $name = trim((string)($body['name'] ?? ''));
  $address = trim((string)($body['address'] ?? ''));
  $debt = ffloat($body['debt_to_supplier'] ?? 0);

  if ($name === '') json_response(['ok' => false, 'error' => 'Nome é obrigatório.'], 422);

  $st = $pdo->prepare("UPDATE suppliers SET name=:n, address=:a, debt_to_supplier=:d WHERE id=:id");
  $st->execute([':n'=>$name, ':a'=>($address===''?null:$address), ':d'=>$debt, ':id'=>$id]);
  json_response(['ok' => true]);
}

if ($method === 'DELETE') {
  $id = (int)($body['id'] ?? 0);
  if ($id <= 0) json_response(['ok' => false, 'error' => 'ID inválido.'], 422);

  $st = $pdo->prepare("DELETE FROM suppliers WHERE id=:id");
  $st->execute([':id'=>$id]);
  json_response(['ok' => true]);
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);