<?php
declare(strict_types=1);

$active = 'relatorios';
$title = 'Relatórios • PDV Info85';
$header = 'Relatórios';
$subheader = 'Total de vendas • lucro líquido por período';

ob_start();
?>
<div class="card card-app mb-3">
  <div class="card-body">
    <div class="row g-3 align-items-end">
      <div class="col-12 col-md-3">
        <label class="form-label text-secondary small">De</label>
        <input type="date" class="form-control" id="r_from">
      </div>
      <div class="col-12 col-md-3">
        <label class="form-label text-secondary small">Até</label>
        <input type="date" class="form-control" id="r_to">
      </div>
      <div class="col-12 col-md-3 d-grid">
        <button class="btn btn-primary" id="btnRunReport">Gerar</button>
      </div>
    </div>
  </div>
</div>

<div class="row g-3">
  <div class="col-12 col-lg-4">
    <div class="card card-app"><div class="card-body">
      <div class="text-secondary small">Total de vendas (OK)</div>
      <div class="h4 fw-bold mb-0" id="r_total_sales">—</div>
    </div></div>
  </div>
  <div class="col-12 col-lg-4">
    <div class="card card-app"><div class="card-body">
      <div class="text-secondary small">Lucro líquido</div>
      <div class="h4 fw-bold mb-0" id="r_profit">—</div>
    </div></div>
  </div>
  <div class="col-12 col-lg-4">
    <div class="card card-app"><div class="card-body">
      <div class="text-secondary small">Desconto total</div>
      <div class="h4 fw-bold mb-0" id="r_discount">—</div>
    </div></div>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';