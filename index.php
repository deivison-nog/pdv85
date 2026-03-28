<?php
declare(strict_types=1);

require_once __DIR__ . '/app/bootstrap.php';
require_once __DIR__ . '/app/auth.php';

bootstrap_app();

if (current_user()) {
  header('Location: dashboard.php');
  exit;
}
header('Location: login.php');
exit;