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

function is_duplicate_key(Throwable $e): bool {
  // MySQL duplicate entry: SQLSTATE 23000 / error 1062
  if ($e instanceof PDOException) {
    $code = (string)$e->getCode();
    if ($code === '23000') return true;
    // Alguns drivers colocam 1062 em errorInfo[1]
    if (isset($e->errorInfo[1]) && (int)$e->errorInfo[1] === 1062) return true;
  }
  return false;
}

if ($method === 'GET') {
  $q = trim((string)($_GET['q'] ?? ''));
  $limit = (int)($_GET['limit'] ?? 50);
  $limit = max(1, min(200, $limit));

  $baseSelect = "
    SELECT
      p.id, p.name, p.upc, p.cost_price, p.price, p.stock, p.category_id,
      c.name AS category
    FROM products p
    LEFT JOIN categories c ON c.id = p.category_id
  ";

  if ($q === '') {
    $stmt = $pdo->prepare($baseSelect . " ORDER BY p.id DESC LIMIT :l");
    $stmt->bindValue(':l', $limit, PDO::PARAM_INT);
    $stmt->execute();
    json_response(['ok' => true, 'data' => $stmt->fetchAll()]);
  }

  if (is_digits($q)) {
    $upc = normalize_upc($q);
    $stmt = $pdo->prepare($baseSelect . "
      WHERE p.upc LIKE :upc
      ORDER BY (p.upc = :exact) DESC, p.name ASC
      LIMIT :l
    ");
    $stmt->bindValue(':upc', $upc . '%');
    $stmt->bindValue(':exact', $upc);
    $stmt->bindValue(':l', $limit, PDO::PARAM_INT);
    $stmt->execute();
    json_response(['ok' => true, 'data' => $stmt->fetchAll()]);
  }

  $stmt = $pdo->prepare($baseSelect . "
    WHERE p.name LIKE :name
    ORDER BY p.name ASC
    LIMIT :l
  ");
  $stmt->bindValue(':name', '%' . $q . '%');
  $stmt->bindValue(':l', $limit, PDO::PARAM_INT);
  $stmt->execute();
  json_response(['ok' => true, 'data' => $stmt->fetchAll()]);
}

$body = post_json();

if ($method === 'POST') {
  $name = trim((string)($body['name'] ?? ''));
  $upc = normalize_upc($body['upc'] ?? null);
  $cost = ffloat($body['cost_price'] ?? 0);
  $price = ffloat($body['price'] ?? 0);
  $stock = (int)($body['stock'] ?? 0);
  $category_id = isset($body['category_id']) && $body['category_id'] !== '' ? (int)$body['category_id'] : null;

  if ($name === '') json_response(['ok' => false, 'error' => 'Nome é obrigatório.'], 422);

  try {
    $stmt = $pdo->prepare("
      INSERT INTO products (name, upc, cost_price, price, stock, category_id)
      VALUES (:n, :u, :c, :p, :s, :cid)
    ");
    $stmt->execute([
      ':n' => $name,
      ':u' => $upc,
      ':c' => $cost,
      ':p' => $price,
      ':s' => $stock,
      ':cid' => $category_id,
    ]);
    json_response(['ok' => true, 'id' => (int)$pdo->lastInsertId()]);
  } catch (Throwable $e) {
    if (is_duplicate_key($e)) {
      json_response(['ok' => false, 'error' => 'UPC já cadastrado.'], 409);
    }
    json_response(['ok' => false, 'error' => $e->getMessage()], 500);
  }
}

if ($method === 'PUT') {
  $id = (int)($body['id'] ?? 0);
  if ($id <= 0) json_response(['ok' => false, 'error' => 'ID inválido.'], 422);

  $name = trim((string)($body['name'] ?? ''));
  $upc = normalize_upc($body['upc'] ?? null);
  $cost = ffloat($body['cost_price'] ?? 0);
  $price = ffloat($body['price'] ?? 0);
  $stock = (int)($body['stock'] ?? 0);
  $category_id = isset($body['category_id']) && $body['category_id'] !== '' ? (int)$body['category_id'] : null;

  if ($name === '') json_response(['ok' => false, 'error' => 'Nome é obrigatório.'], 422);

  try {
    $stmt = $pdo->prepare("
      UPDATE products
      SET name=:n, upc=:u, cost_price=:c, price=:p, stock=:s, category_id=:cid
      WHERE id=:id
    ");
    $stmt->execute([
      ':n' => $name,
      ':u' => $upc,
      ':c' => $cost,
      ':p' => $price,
      ':s' => $stock,
      ':cid' => $category_id,
      ':id' => $id,
    ]);
    json_response(['ok' => true]);
  } catch (Throwable $e) {
    if (is_duplicate_key($e)) {
      json_response(['ok' => false, 'error' => 'UPC já cadastrado.'], 409);
    }
    json_response(['ok' => false, 'error' => $e->getMessage()], 500);
  }
}

if ($method === 'DELETE') {
  $id = (int)($body['id'] ?? 0);
  if ($id <= 0) json_response(['ok' => false, 'error' => 'ID inválido.'], 422);

  $stmt = $pdo->prepare("DELETE FROM products WHERE id=:id");
  $stmt->execute([':id' => $id]);
  json_response(['ok' => true]);
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);