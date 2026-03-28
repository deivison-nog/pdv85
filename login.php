<?php
declare(strict_types=1);

require_once __DIR__ . '/app/bootstrap.php';
require_once __DIR__ . '/app/auth.php';

bootstrap_app();

if (current_user()) {
  header('Location: dashboard.php');
  exit;
}

$error = null;
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $u = trim((string)($_POST['username'] ?? ''));
  $p = (string)($_POST['password'] ?? '');
  if (login($u, $p)) {
    header('Location: dashboard.php');
    exit;
  }
  $error = 'Usuário ou senha inválidos.';
}
?>
<!doctype html>
<html lang="pt-br" data-theme="dark">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Login • PDV Info85</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="assets/css/app.css" rel="stylesheet">
</head>
<body class="bg-app">
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-12 col-md-7 col-lg-5">
        <div class="card card-app shadow-lg border-0">
          <div class="card-body p-4">
            <div class="d-flex align-items-center gap-2 mb-3">
              <div class="brand-badge" style="width:38px;height:38px;border-radius:14px"></div>
              <div>
                <div class="fw-bold">PDV Info85</div>
                <div class="text-secondary small">Acesso ao sistema</div>
              </div>
            </div>

            <?php if ($error): ?>
              <div class="alert alert-danger"><?= escape($error) ?></div>
            <?php endif; ?>

            <form method="post" autocomplete="off">
              <div class="mb-3">
                <label class="form-label">Usuário</label>
                <input name="username" class="form-control form-control-lg" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Senha</label>
                <input name="password" type="password" class="form-control form-control-lg" required>
              </div>
              <button class="btn btn-primary btn-lg w-100">Entrar</button>
            </form>

            <hr class="my-4">
            <div class="small text-secondary">
              Padrão: <b>administrador</b> / <b>admin123</b>
            </div>
          </div>
        </div>
        <div class="text-center mt-3 small text-secondary">http://localhost/pdv-info85/</div>
      </div>
    </div>
  </div>
</body>
</html>