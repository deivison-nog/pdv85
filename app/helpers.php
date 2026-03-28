<?php
declare(strict_types=1);

function json_response(array $data, int $status = 200): void {
  http_response_code($status);
  header('Content-Type: application/json; charset=utf-8');
  echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
  exit;
}

function post_json(): array {
  $raw = file_get_contents('php://input');
  $data = json_decode($raw ?: '[]', true);
  return is_array($data) ? $data : [];
}

function is_digits(string $s): bool {
  return $s !== '' && preg_match('/^\d+$/', $s) === 1;
}

function normalize_upc(?string $upc): ?string {
  if ($upc === null) return null;
  $u = preg_replace('/\D+/', '', $upc);
  return ($u === '') ? null : $u;
}

function brl(float $v): string {
  return 'R$ ' . number_format($v, 2, ',', '.');
}

function ffloat(mixed $v): float {
  if ($v === null) return 0.0;
  $s = str_replace(',', '.', preg_replace('/[^\d\-,\.]/', '', (string)$v));
  return (float)$s;
}

function escape(string $s): string {
  return htmlspecialchars($s, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}