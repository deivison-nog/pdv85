<?php
declare(strict_types=1);

return [
  'app_name' => 'PDV Info85',
  'base_url' => '/pdv-info85',

  'db' => [
    'host' => '127.0.0.1',
    'name' => 'pdv85',
    'user' => 'root',
    'pass' => '',
    'charset' => 'utf8mb4',
  ],

  'session_name' => 'pdvinfo85_session',

  'seed_admin' => [
    'username' => 'administrador',
    'password' => 'admin123',
    'role' => 'admin',
  ],

  'low_stock_threshold' => 5,
];