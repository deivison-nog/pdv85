<?php
declare(strict_types=1);

$active = 'etiquetas';
$title = 'Etiquetas • PDV Info85';
$header = 'Etiquetas';
$subheader = 'Gerar e imprimir etiquetas de produtos';

ob_start();
?>
<style>
  /* ===== Label (screen + print) ===== */
  .etq-label {
    width: 60mm;
    height: 40mm;
    border: 2px solid #cc0000;
    border-radius: 3px;
    padding: 3mm 3.5mm;
    background: #fff;
    color: #000;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    text-align: center;
    text-transform: uppercase;
    box-sizing: border-box;
    font-family: Arial, Helvetica, sans-serif;
    overflow: hidden;
    flex-shrink: 0;
  }
  .etq-company { font-size: 7.5pt; color: #cc0000; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%; letter-spacing: 0.4px; }
  .etq-name    { font-size: 10pt; font-weight: 700; line-height: 1.2; max-height: 2.5em; overflow: hidden; width: 100%; }
  .etq-price   { font-size: 20pt; font-weight: 800; line-height: 1; color: #cc0000; }
  .etq-upc     { font-size: 7pt; color: #555; letter-spacing: 1px; font-family: monospace; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%; border-top: 1px dashed #cc0000; padding-top: 0.8mm; }

  /* Queue item badge */
  .etq-queue-item {
    background: var(--panel2);
    border: 1px solid var(--stroke);
    color: var(--text);
    font-weight: normal;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.35rem 0.5rem;
    border-radius: 0.5rem;
  }

  /* ===== Screen only ===== */
  @media screen {
    #etqPrintArea { display: none; }
    #etqLabelsWrap { background: #e5e7eb; border-radius: 8px; padding: 12px; }
  }

  /* ===== Print only ===== */
  @media print {
    .sidebar,
    .topbar,
    #etqSearchSection,
    #etqResultsCard,
    #etqSelectedWrapper,
    #etqPreviewHeader { display: none !important; }

    body, .bg-app { background: #fff !important; }
    main { padding: 0 !important; }
    .d-flex.flex-grow-1 { display: block !important; }
    #etqLabelsWrap { display: none !important; }

    #etqPrintArea {
      display: flex !important;
      flex-wrap: wrap;
      gap: 2mm;
      padding: 3mm;
    }

    @page { margin: 5mm; size: auto; }
  }
</style>

<!-- Search Section -->
<div id="etqSearchSection" class="card card-app mb-3">
  <div class="card-body">
    <div class="row g-3 align-items-end">
      <div class="col-12 col-md-8">
        <label class="form-label">Buscar produto</label>
        <div class="input-group">
          <input class="form-control" id="etqSearch" placeholder="Nome ou código de barras...">
          <button class="btn btn-primary" id="btnEtqSearch" type="button">Buscar</button>
        </div>
      </div>
      <div class="col-12 col-md-4 d-grid">
        <button class="btn btn-success" id="btnPrintLabels" type="button" disabled>🖨 Imprimir Etiquetas</button>
      </div>
    </div>
  </div>
</div>

<!-- Search Results -->
<div id="etqResultsCard" class="card card-app mb-3 d-none">
  <div class="table-responsive">
    <table class="table table-app table-hover mb-0">
      <thead>
        <tr>
          <th>Produto</th>
          <th>UPC</th>
          <th class="text-end">Preço</th>
          <th></th>
        </tr>
      </thead>
      <tbody id="etqResultsBody"></tbody>
    </table>
  </div>
</div>

<!-- Print Queue -->
<div id="etqSelectedWrapper" class="card card-app mb-3 d-none">
  <div class="card-body">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <span class="fw-bold small">Fila de impressão</span>
      <button class="btn btn-outline-danger btn-sm" id="btnClearQueue" type="button">Limpar</button>
    </div>
    <div id="etqSelectedList" class="d-flex flex-wrap gap-2"></div>
  </div>
</div>

<!-- Preview -->
<div id="etqPreviewWrapper" class="d-none">
  <div id="etqPreviewHeader" class="d-flex justify-content-between align-items-center mb-2">
    <span class="fw-bold">Prévia das etiquetas</span>
    <span class="text-secondary small" id="etqLabelCount"></span>
  </div>
  <div id="etqLabelsWrap">
    <div id="etqLabels" class="d-flex flex-wrap gap-2"></div>
  </div>
</div>

<!-- Print area (hidden on screen, visible on print) -->
<div id="etqPrintArea"></div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';
