<?php
declare(strict_types=1);

require_once __DIR__ . '/app/config.php';
$cfg = require __DIR__ . '/app/config.php';

$active = 'produtos';
$title = 'Produtos • PDV Info85';
$header = 'Produtos';
$subheader = 'UPC • custo • venda • % ganho';

ob_start();
?>
<div class="d-flex justify-content-between align-items-center mb-3">
  <div class="fw-bold">Cadastro de Produtos</div>
  <button class="btn btn-primary" id="btnNewProduct" data-bs-toggle="modal" data-bs-target="#productModal">Novo Produto</button>
</div>

<div class="row g-3 mb-3">
  <div class="col-12 col-md-6">
    <input class="form-control" id="prodSearch" placeholder="Buscar por nome ou UPC...">
  </div>
  <div class="col-12 col-md-4">
    <select class="form-select" id="prodCategory"></select>
  </div>
  <div class="col-12 col-md-2 d-grid">
    <button class="btn btn-outline-light" id="btnRefresh">Atualizar</button>
  </div>
</div>

<div class="card card-app">
  <div class="table-responsive">
    <table class="table table-app table-hover mb-0" id="prodTable">
      <thead>
        <tr>
          <th>Produto</th>
          <th>UPC</th>
          <th>Categoria</th>
          <th class="text-end">Custo</th>
          <th class="text-end">Venda</th>
          <th class="text-end">% ganho</th>
          <th class="text-end">Estoque</th>
          <th class="text-end">Ações</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
  </div>
</div>

<div class="modal fade" id="productModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content bg-panel border-0">
      <div class="modal-header">
        <h5 class="modal-title" id="productModalTitle">Novo Produto</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="p_id">

        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">Nome</label>
            <input class="form-control" id="p_name">
          </div>
          <div class="col-12 col-md-3">
            <label class="form-label">UPC</label>
            <input class="form-control" id="p_upc">
          </div>
          <div class="col-12 col-md-3">
            <label class="form-label">Categoria</label>
            <select class="form-select" id="p_category"></select>
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">Preço de custo</label>
            <input class="form-control" id="p_cost" inputmode="decimal">
          </div>
          <div class="col-12 col-md-4">
            <label class="form-label">Preço de venda</label>
            <input class="form-control" id="p_price" inputmode="decimal">
          </div>
          <div class="col-12 col-md-4">
            <label class="form-label">% de ganho</label>
            <input class="form-control" id="p_gain" disabled>
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">Estoque</label>
            <input class="form-control" id="p_stock" type="number" min="0" step="1" value="0">
          </div>
        </div>

        <div class="alert alert-danger mt-3 d-none" id="p_error"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline-light" data-bs-dismiss="modal">Cancelar</button>
        <button class="btn btn-primary" id="btnSaveProduct">Salvar</button>
      </div>
    </div>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';
