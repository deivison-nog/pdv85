<?php
declare(strict_types=1);

$active = 'clientes';
$title = 'Clientes • PDV Info85';
$header = 'Clientes';
$subheader = 'Dívida do cliente (cliente deve para a loja)';

ob_start();
?>
<div class="d-flex justify-content-between align-items-center mb-3">
  <div class="fw-bold">Controle de Clientes</div>
  <button class="btn btn-primary" id="btnNewClient" data-bs-toggle="modal" data-bs-target="#clientModal">Novo Cliente</button>
</div>

<div class="row g-3 mb-3">
  <div class="col-12 col-md-8">
    <input class="form-control" id="clientSearch" placeholder="Buscar cliente...">
  </div>
  <div class="col-12 col-md-4 d-grid">
    <button class="btn btn-outline-light" id="btnRefreshClients">Atualizar</button>
  </div>
</div>

<div class="card card-app">
  <div class="table-responsive">
    <table class="table table-dark table-hover mb-0" id="clientsTable">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Endereço</th>
          <th class="text-end">Dívida (R$)</th>
          <th class="text-end">Ações</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
  </div>
</div>

<div class="modal fade" id="clientModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content bg-panel border-0">
      <div class="modal-header">
        <h5 class="modal-title" id="clientModalTitle">Novo Cliente</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="c_id">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">Nome (obrigatório)</label>
            <input class="form-control" id="c_name">
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">Endereço</label>
            <input class="form-control" id="c_address">
          </div>
          <div class="col-12 col-md-4">
            <label class="form-label">Dívida</label>
            <input class="form-control" id="c_debt" inputmode="decimal" value="0">
          </div>
        </div>
        <div class="alert alert-danger mt-3 d-none" id="c_error"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline-light" data-bs-dismiss="modal">Cancelar</button>
        <button class="btn btn-primary" id="btnSaveClient">Salvar</button>
      </div>
    </div>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';