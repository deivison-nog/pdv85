<?php
declare(strict_types=1);

$active = 'config';
$title = 'Configurações • PDV Info85';
$header = 'Configurações';
$subheader = 'Empresa • Cupom • Tema';

ob_start();
?>
<div class="card card-app">
  <div class="card-body">
    <div class="d-flex justify-content-end gap-2 mb-3">
      <button class="btn btn-outline-light" id="btnReloadSettings">Recarregar</button>
      <button class="btn btn-primary" id="btnSaveSettings">Salvar</button>
    </div>

    <ul class="nav nav-tabs" role="tablist">
      <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tabEmpresa">Empresa</button></li>
      <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabCupom">Cupom</button></li>
      <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabTema">Tema</button></li>
    </ul>

    <div class="tab-content pt-3">
      <div class="tab-pane fade show active" id="tabEmpresa">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">Nome</label>
            <input class="form-control" id="s_company_name">
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">CNPJ</label>
            <input class="form-control" id="s_company_cnpj">
          </div>
        </div>
      </div>

      <div class="tab-pane fade" id="tabCupom">
        <div class="row g-3">
          <div class="col-12 col-md-4">
            <label class="form-label">Largura (mm)</label>
            <input class="form-control" id="s_coupon_width_mm" value="58">
          </div>
          <div class="col-12 col-md-4">
            <label class="form-label">Nº de vias</label>
            <input class="form-control" id="s_coupon_copies" value="2">
          </div>
          <div class="col-12 col-md-4">
            <label class="form-label">Auto imprimir</label>
            <select class="form-select" id="s_coupon_auto_print">
              <option value="1">Sim</option>
              <option value="0">Não</option>
            </select>
          </div>
        </div>
      </div>

      <div class="tab-pane fade" id="tabTema">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">Tema</label>
            <select class="form-select" id="s_theme">
              <option value="dark">Dark</option>
              <option value="light">Light</option>
            </select>
            <div class="text-secondary small mt-2">
              Toggle rápido: botão “Dark/Light” no menu.
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="alert alert-success mt-3 d-none" id="settingsOk">Salvo com sucesso.</div>
    <div class="alert alert-danger mt-3 d-none" id="settingsErr"></div>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';