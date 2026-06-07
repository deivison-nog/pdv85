<?php
declare(strict_types=1);

return [
  'app_name' => 'PDV Info85',
  'base_url' => '/pdv-info85',

  'db' => [
    'host' => 'srv2036.hstgr.io',
    'name' => 'u641927335_pdv85',
    'user' => 'u641927335_pdv85',
    'pass' => 'pdvDEO01+',
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