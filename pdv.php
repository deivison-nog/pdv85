<?php
declare(strict_types=1);

$active = 'pdv';
$title = 'PDV / Caixa • PDV Info85';
$header = 'Caixa - PDV';
$subheader = 'UPC • carrinho • dinheiro com troco • cupom em modal';

$body_class = 'pdv-force-light';

ob_start();
?>
<div class="row g-3">
  <div class="col-12 col-lg-7">
    <div class="card card-app">
      <div class="card-body">
        <div class="fw-bold mb-2">Buscar produto</div>
        <input id="pdvSearch" class="form-control form-control-lg" autofocus>
        <div class="mt-3 list-group" id="pdvResults"></div>
      </div>
    </div>
  </div>

  <div class="col-12 col-lg-5">
    <div class="card card-app">
      <div class="card-body">
        <div class="fw-bold mb-2">Carrinho</div>

        <div id="cartItems" class="vstack gap-2 mb-3"></div>

        <div class="d-flex justify-content-between">
          <div>Subtotal</div>
          <div class="fw-bold" id="cartSubtotal">R$ 0,00</div>
        </div>

        <div class="mt-2">
          <label class="form-label small text-secondary mb-1">Desconto no total (R$)</label>
          <input id="cartDiscount" class="form-control" value="0">
        </div>

        <div class="d-flex justify-content-between mt-2">
          <div>Total</div>
          <div class="fw-bold fs-5" id="cartTotal">R$ 0,00</div>
        </div>

        <hr>

        <label class="form-label small text-secondary mb-1">Pagamento</label>
        <select id="payMethod" class="form-select mb-2">
          <option value="DINHEIRO">Dinheiro</option>
          <option value="PIX" selected>PIX</option>
          <option value="DEBITO">Débito</option>
          <option value="CREDITO">Crédito</option>
        </select>

        <button class="btn btn-primary btn-lg w-100" id="btnFinalize" disabled>Finalizar venda</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal Dinheiro -->
<div class="modal fade" id="cashModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-md modal-dialog-centered">
    <div class="modal-content bg-panel border-0">
      <div class="modal-header">
        <h5 class="modal-title">Pagamento em Dinheiro</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="mb-2 text-secondary small">Total da venda: <b id="cashTotalLabel"></b></div>

        <label class="form-label">Valor pago</label>
        <input class="form-control" id="cashPaid" inputmode="decimal">

        <div class="mt-2">
          <div class="text-secondary small">Troco:</div>
          <div class="fw-bold fs-4" id="cashChangeLabel">R$ 0,00</div>
        </div>

        <div class="alert alert-danger mt-3 d-none" id="cashErr"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline-light" data-bs-dismiss="modal">Voltar</button>
        <button class="btn btn-primary" id="btnCashConfirm">Confirmar</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal Cupom (iframe) -->
<div class="modal fade" id="couponModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content bg-panel border-0">
      <div class="modal-header">
        <h5 class="modal-title">Cupom</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <iframe id="couponFrame" style="width:100%; height:70vh; border:1px solid var(--stroke); border-radius:12px;"></iframe>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline-light" data-bs-dismiss="modal">Fechar</button>
        <button class="btn btn-primary" id="btnPrintCoupon">Imprimir</button>
      </div>
    </div>
  </div>
</div>
<?php
$content = ob_get_clean();
require __DIR__ . '/_layout.php';