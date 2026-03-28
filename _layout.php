<?php
declare(strict_types=1);

require_once __DIR__ . '/app/bootstrap.php';
require_once __DIR__ . '/app/auth.php';
require_once __DIR__ . '/app/db.php';
require_once __DIR__ . '/app/helpers.php';

bootstrap_app();
require_auth();

$cfg = require __DIR__ . '/app/config.php';
$user = current_user();

$active = $active ?? 'dashboard';
$title  = $title  ?? $cfg['app_name'];
$header = $header ?? '';
$subheader = $subheader ?? '';

$pdo = db();
$themeRow = $pdo->query("SELECT `value` FROM settings WHERE `key`='theme' LIMIT 1")->fetchColumn();
$theme = $themeRow ?: 'dark';

$body_class = $body_class ?? '';
?>
<!doctype html>
<html lang="pt-br" data-theme="<?= escape($theme) ?>">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><?= escape($title) ?></title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="assets/css/app.css" rel="stylesheet">
</head>
<body class="bg-app <?= escape($body_class) ?>">
  <div class="d-flex">
    <aside class="sidebar p-3">
      <div class="brand mb-3">
        <div class="brand-badge"></div>
        <div>
          <div class="fw-bold"><?= escape($cfg['app_name']) ?></div>
          <div class="text-secondary small">Site PDV</div>
        </div>
      </div>

      <div class="p-3 rounded-4 bg-panel mb-3">
        <div>Olá, <b><?= escape($user['username'] ?? '') ?></b></div>
        <div class="text-secondary small">Perfil: admin</div>
      </div>

      <ul class="nav nav-pills flex-column gap-1">
        <li class="nav-item"><a class="nav-link <?= $active==='dashboard'?'active':'' ?>" href="dashboard.php">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='pdv'?'active':'' ?>" href="pdv.php">PDV / Caixa</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='produtos'?'active':'' ?>" href="produtos.php">Produtos</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='clientes'?'active':'' ?>" href="clientes.php">Clientes</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='fornecedores'?'active':'' ?>" href="fornecedores.php">Fornecedores</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='historico'?'active':'' ?>" href="historico.php">Histórico</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='relatorios'?'active':'' ?>" href="relatorios.php">Relatórios</a></li>
        <li class="nav-item"><a class="nav-link <?= $active==='config'?'active':'' ?>" href="configuracoes.php">Configurações</a></li>
      </ul>

      <div class="mt-auto pt-3 d-flex gap-2">
        <button class="btn btn-outline-light w-100" id="themeToggleBtn" type="button">Dark/Light</button>
        <a class="btn btn-outline-light w-100" href="logout.php">Sair</a>
      </div>
    </aside>

    <main class="flex-grow-1 p-4">
      <div class="topbar mb-4">
        <div>
          <h4 class="mb-0"><?= escape($header) ?></h4>
          <div class="text-secondary small"><?= escape($subheader) ?></div>
        </div>
        <div class="d-flex gap-2">
          <a class="btn btn-primary" href="pdv.php">Nova Venda</a>
          <a class="btn btn-outline-light" href="produtos.php">Novo Produto</a>
        </div>
      </div>

      <?= $content ?? '' ?>
    </main>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="assets/js/app.js"></script>
</body>
</html>