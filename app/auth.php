<?php
declare(strict_types=1);

require_once __DIR__ . '/db.php';

function start_app_session(): void {
  $cfg = require __DIR__ . '/config.php';
  if (session_status() !== PHP_SESSION_ACTIVE) {
    session_name($cfg['session_name']);
    session_start();
  }
}

function current_user(): ?array {
  start_app_session();
  return $_SESSION['user'] ?? null;
}

function require_auth(): void {
  if (!current_user()) {
    $isApi = !empty($_SERVER['HTTP_X_REQUESTED_WITH'])
      && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) === 'xmlhttprequest';
    if ($isApi) {
      http_response_code(401);
      header('Content-Type: application/json; charset=utf-8');
      echo json_encode(['ok' => false, 'error' => 'Não autenticado'], JSON_UNESCAPED_UNICODE);
      exit;
    }
    header('Location: login.php');
    exit;
  }
}

function login(string $username, string $password): bool {
  start_app_session();
  $pdo = db();

  $stmt = $pdo->prepare("SELECT id, username, password_hash, role FROM users WHERE username = :u LIMIT 1");
  $stmt->execute([':u' => $username]);
  $user = $stmt->fetch();

  if (!$user) return false;
  if (!password_verify($password, $user['password_hash'])) return false;

  $_SESSION['user'] = [
    'id' => (int)$user['id'],
    'username' => $user['username'],
    'role' => $user['role'],
  ];
  return true;
}

function logout(): void {
  start_app_session();
  $_SESSION = [];
  session_destroy();
}