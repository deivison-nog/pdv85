<?php
declare(strict_types=1);

$active = 'historico';
$title = 'Histórico • PDV Info85';
$header = 'Histórico de Vendas';
$subheader = 'Imprimir • Cancelar';

ob_start();
?>
<div class="card card-app">
  <div class="table-responsive">
    <table class="table table-dark table-hover mb-0" id="salesTable">
      <thead>
        <tr>
          <th>#</th><th>Data</th><th>Pagamento</th><th>Status</th>
          <th class="text-end">Desconto</th><th class="text-end">Total</th>
          <th class="text-end">Ações</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';