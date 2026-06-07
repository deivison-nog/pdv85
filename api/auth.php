<?php
declare(strict_types=1);

require_once __DIR__ . '/../app/bootstrap.php';
require_once __DIR__ . '/../app/auth.php';
require_once __DIR__ . '/../app/helpers.php';

bootstrap_app();

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
  $user = current_user();
  if ($user) {
    json_response(['ok' => true, 'user' => $user]);
  }
  json_response(['ok' => false, 'error' => 'Não autenticado'], 401);
}

if ($method === 'POST') {
  $body = post_json();
  $username = trim((string)($body['username'] ?? ''));
  $password = (string)($body['password'] ?? '');

  if ($username === '' || $password === '') {
    json_response(['ok' => false, 'error' => 'Credenciais obrigatórias.'], 422);
  }

  if (login($username, $password)) {
    json_response(['ok' => true, 'user' => current_user()]);
  }

  json_response(['ok' => false, 'error' => 'Usuário ou senha inválidos.'], 401);
}

if ($method === 'DELETE') {
  logout();
  json_response(['ok' => true]);
}

json_response(['ok' => false, 'error' => 'Método não suportado'], 405);
