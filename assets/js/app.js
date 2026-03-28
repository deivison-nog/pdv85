const api = {
  async get(url) {
    const r = await fetch(url, { headers: { 'Accept': 'application/json' } });
    return await r.json();
  },
  async send(url, method, data) {
    const r = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify(data ?? {})
    });
    // tenta sempre ler JSON
    try { return await r.json(); } catch(e) { return { ok:false, error:'Resposta inválida' }; }
  }
};

function formatBRL(v){
  return new Intl.NumberFormat('pt-BR', { style:'currency', currency:'BRL' }).format(v || 0);
}
function escapeHtml(s){
  return String(s ?? '')
    .replaceAll('&','&amp;')
    .replaceAll('<','&lt;')
    .replaceAll('>','&gt;')
    .replaceAll('"','&quot;')
    .replaceAll("'","&#039;");
}
function debounce(fn, ms){
  let t; return (...args)=>{ clearTimeout(t); t=setTimeout(()=>fn(...args), ms); };
}

async function toggleTheme(){
  const html = document.documentElement;
  const current = html.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  html.setAttribute('data-theme', next);
  await api.send('api/settings.php', 'POST', { theme: next });
}

document.addEventListener('DOMContentLoaded', async () => {
  const tbtn = document.getElementById('themeToggleBtn');
  if (tbtn) tbtn.addEventListener('click', toggleTheme);

  if (document.getElementById('btnSaveSettings')) initSettings();
  if (document.getElementById('prodTable')) initProdutos();
  if (document.getElementById('pdvSearch')) initPDV();
  if (document.getElementById('salesTable')) initHistorico();
  if (document.getElementById('btnRunReport')) initRelatorios();
  if (document.getElementById('clientsTable')) initClients();
  if (document.getElementById('suppliersTable')) initSuppliers();
});

/* ============ SETTINGS ============ */
async function initSettings(){
  const ok = document.getElementById('settingsOk');
  const err = document.getElementById('settingsErr');

  const fields = {
    company_name: document.getElementById('s_company_name'),
    company_cnpj: document.getElementById('s_company_cnpj'),
    coupon_width_mm: document.getElementById('s_coupon_width_mm'),
    coupon_copies: document.getElementById('s_coupon_copies'),
    coupon_auto_print: document.getElementById('s_coupon_auto_print'),
    theme: document.getElementById('s_theme'),
  };

  async function load(){
    ok.classList.add('d-none');
    err.classList.add('d-none');
    const res = await api.get('api/settings.php');
    if (!res.ok) return;

    for (const k of Object.keys(fields)){
      if (res.data[k] !== undefined) fields[k].value = res.data[k];
    }
    document.documentElement.setAttribute('data-theme', fields.theme.value || 'dark');
  }

  document.getElementById('btnReloadSettings')?.addEventListener('click', load);

  document.getElementById('btnSaveSettings')?.addEventListener('click', async () => {
    ok.classList.add('d-none');
    err.classList.add('d-none');

    const payload = {};
    for (const k of Object.keys(fields)) payload[k] = fields[k].value;

    const res = await api.send('api/settings.php', 'POST', payload);
    if (!res.ok){
      err.textContent = res.error || 'Erro ao salvar';
      err.classList.remove('d-none');
      return;
    }
    document.documentElement.setAttribute('data-theme', fields.theme.value);
    ok.classList.remove('d-none');
  });

  load();
}

/* ============ PRODUTOS (com erro amigável 409) ============ */
async function initProdutos(){
  const tableBody = document.querySelector('#prodTable tbody');
  const search = document.getElementById('prodSearch');
  const categoryFilter = document.getElementById('prodCategory');
  const btnRefresh = document.getElementById('btnRefresh');

  const modalTitle = document.getElementById('productModalTitle');
  const p_id = document.getElementById('p_id');
  const p_name = document.getElementById('p_name');
  const p_upc = document.getElementById('p_upc');
  const p_category = document.getElementById('p_category');
  const p_cost = document.getElementById('p_cost');
  const p_price = document.getElementById('p_price');
  const p_gain = document.getElementById('p_gain');
  const p_stock = document.getElementById('p_stock');
  const p_error = document.getElementById('p_error');

  function calcGain(){
    const cost = Number(String(p_cost.value).replace(',','.')) || 0;
    const sale = Number(String(p_price.value).replace(',','.')) || 0;
    if (cost <= 0) { p_gain.value = '—'; return; }
    const pct = ((sale - cost) / cost) * 100;
    p_gain.value = `${pct.toFixed(2)}%`;
  }
  p_cost?.addEventListener('input', calcGain);
  p_price?.addEventListener('input', calcGain);

  document.getElementById('btnNewProduct')?.addEventListener('click', () => {
    modalTitle.textContent = 'Novo Produto';
    p_id.value = '';
    p_name.value = '';
    p_upc.value = '';
    p_cost.value = '0';
    p_price.value = '0';
    p_stock.value = '0';
    p_category.value = '';
    p_error.classList.add('d-none');
    calcGain();
  });

  async function loadCategories(){
    const res = await api.get('api/categories.php');
    if (!res.ok) return;

    categoryFilter.innerHTML =
      `<option value="">Todas categorias</option>` +
      res.data.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');

    p_category.innerHTML =
      `<option value="">(sem categoria)</option>` +
      res.data.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');
  }

  async function loadProducts(){
    const q = search.value.trim();
    const res = await api.get(`api/products.php?q=${encodeURIComponent(q)}&limit=200`);
    tableBody.innerHTML = '';
    if (!res.ok) return;

    let rows = res.data;
    const catId = categoryFilter.value;
    if (catId) rows = rows.filter(r => String(r.category_id) === String(catId));

    for (const p of rows){
      const cost = Number(p.cost_price || 0);
      const sale = Number(p.price || 0);
      const pct = cost > 0 ? ((sale - cost) / cost) * 100 : null;

      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(p.name)}</td>
        <td>${escapeHtml(p.upc ?? '')}</td>
        <td>${escapeHtml(p.category ?? '')}</td>
        <td class="text-end">${formatBRL(cost)}</td>
        <td class="text-end">${formatBRL(sale)}</td>
        <td class="text-end">${pct === null ? '—' : (pct.toFixed(2) + '%')}</td>
        <td class="text-end">${p.stock}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-light" data-edit="${p.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger" data-del="${p.id}">Excluir</button>
        </td>
      `;
      tableBody.appendChild(tr);

      tr.querySelector('[data-edit]').addEventListener('click', () => {
        modalTitle.textContent = 'Editar Produto';
        p_id.value = p.id;
        p_name.value = p.name ?? '';
        p_upc.value = p.upc ?? '';
        p_cost.value = p.cost_price ?? '0';
        p_price.value = p.price ?? '0';
        p_stock.value = p.stock ?? 0;
        p_category.value = p.category_id ?? '';
        p_error.classList.add('d-none');
        calcGain();
        new bootstrap.Modal(document.getElementById('productModal')).show();
      });

      tr.querySelector('[data-del]').addEventListener('click', async () => {
        if (!confirm('Excluir produto?')) return;
        const r = await api.send('api/products.php', 'DELETE', { id: p.id });
        if (!r.ok) { alert(r.error || 'Erro'); return; }
        loadProducts();
      });
    }
  }

  document.getElementById('btnSaveProduct')?.addEventListener('click', async () => {
    p_error.classList.add('d-none');

    const payload = {
      id: p_id.value ? Number(p_id.value) : undefined,
      name: p_name.value.trim(),
      upc: p_upc.value.trim(),
      category_id: p_category.value ? Number(p_category.value) : '',
      cost_price: p_cost.value,
      price: p_price.value,
      stock: Number(p_stock.value || 0),
    };

    const method = payload.id ? 'PUT' : 'POST';
    const res = await api.send('api/products.php', method, payload);

    if (!res.ok){
      // aqui pega mensagem amigável "UPC já cadastrado."
      p_error.textContent = res.error || 'Erro ao salvar';
      p_error.classList.remove('d-none');
      return;
    }

    bootstrap.Modal.getInstance(document.getElementById('productModal'))?.hide();
    loadProducts();
  });

  btnRefresh?.addEventListener('click', loadProducts);
  search?.addEventListener('input', debounce(loadProducts, 250));
  categoryFilter?.addEventListener('change', loadProducts);

  await loadCategories();
  await loadProducts();
}

/* ============ PDV (Enter adiciona match exato) ============ */
function initPDV(){
  const search = document.getElementById('pdvSearch');
  const results = document.getElementById('pdvResults');
  const cartItemsEl = document.getElementById('cartItems');
  const cartSubtotalEl = document.getElementById('cartSubtotal');
  const cartDiscountEl = document.getElementById('cartDiscount');
  const cartTotalEl = document.getElementById('cartTotal');
  const payMethodEl = document.getElementById('payMethod');
  const btnFinalize = document.getElementById('btnFinalize');

  const cashModalEl = document.getElementById('cashModal');
  const cashTotalLabel = document.getElementById('cashTotalLabel');
  const cashPaid = document.getElementById('cashPaid');
  const cashChangeLabel = document.getElementById('cashChangeLabel');
  const cashErr = document.getElementById('cashErr');
  const btnCashConfirm = document.getElementById('btnCashConfirm');

  const couponModalEl = document.getElementById('couponModal');
  const couponFrame = document.getElementById('couponFrame');
  const btnPrintCoupon = document.getElementById('btnPrintCoupon');

  const cart = new Map(); // id -> {product, qty}
  let lastSaleId = null;

  function compute(){
    let subtotal = 0;
    for (const it of cart.values()) subtotal += (Number(it.product.price) * it.qty);
    const discount = Number(String(cartDiscountEl.value).replace(',','.')) || 0;
    const total = Math.max(0, subtotal - Math.max(0, discount));

    cartSubtotalEl.textContent = formatBRL(subtotal);
    cartTotalEl.textContent = formatBRL(total);
    btnFinalize.disabled = cart.size === 0;
    return { subtotal, discount: Math.max(0, discount), total };
  }

  function renderCart(){
    cartItemsEl.innerHTML = '';
    for (const it of cart.values()){
      const row = document.createElement('div');
      row.className = 'd-flex justify-content-between align-items-center p-2 rounded-3 border';
      row.style.borderColor = 'var(--stroke)';
      row.innerHTML = `
        <div class="me-2">
          <div class="fw-bold">${escapeHtml(it.product.name)}</div>
          <div class="small text-secondary">${escapeHtml(it.product.upc ?? '')}</div>
        </div>
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-sm btn-outline-secondary" data-dec="${it.product.id}">-</button>
          <div class="fw-bold">${it.qty}</div>
          <button class="btn btn-sm btn-outline-secondary" data-inc="${it.product.id}">+</button>
          <div style="width:110px" class="text-end fw-bold">${formatBRL(Number(it.product.price) * it.qty)}</div>
          <button class="btn btn-sm btn-outline-danger" data-del="${it.product.id}">x</button>
        </div>
      `;
      cartItemsEl.appendChild(row);
    }

    cartItemsEl.querySelectorAll('[data-inc]').forEach(b => b.addEventListener('click', () => {
      const id = Number(b.getAttribute('data-inc'));
      cart.get(id).qty++;
      renderCart(); compute();
    }));
    cartItemsEl.querySelectorAll('[data-dec]').forEach(b => b.addEventListener('click', () => {
      const id = Number(b.getAttribute('data-dec'));
      const it = cart.get(id);
      it.qty = Math.max(1, it.qty - 1);
      renderCart(); compute();
    }));
    cartItemsEl.querySelectorAll('[data-del]').forEach(b => b.addEventListener('click', () => {
      const id = Number(b.getAttribute('data-del'));
      cart.delete(id);
      renderCart(); compute();
    }));
  }

  function addToCart(p){
    const id = Number(p.id);
    if (!cart.has(id)) cart.set(id, { product: p, qty: 1 });
    else cart.get(id).qty++;
    renderCart(); compute();
  }

  async function searchProducts(q){
    const res = await api.get(`api/products.php?q=${encodeURIComponent(q)}&limit=20`);
    results.innerHTML = '';
    if (!res.ok) return;

    for (const p of res.data){
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
      btn.innerHTML = `
        <div>
          <div class="fw-bold">${escapeHtml(p.name)}</div>
          <div class="small text-secondary">${escapeHtml(p.upc ?? '')} • Estoque: ${p.stock}</div>
        </div>
        <div class="fw-bold">${formatBRL(Number(p.price))}</div>
      `;
      btn.addEventListener('click', () => {
        addToCart(p);
        search.value = '';
        results.innerHTML = '';
        search.focus();
      });
      results.appendChild(btn);
    }
  }

  // ENTER: se for numérico, tenta match exato e adiciona direto
  search.addEventListener('keydown', async (e) => {
    if (e.key !== 'Enter') return;
    const q = search.value.trim();
    if (q.length === 0) return;

    // normalmente leitor manda só números
    const onlyDigits = /^\d+$/.test(q);
    if (!onlyDigits) return;

    e.preventDefault();

    const res = await api.get(`api/products.php?q=${encodeURIComponent(q)}&limit=10`);
    if (!res.ok || !Array.isArray(res.data)) return;

    const exact = res.data.find(p => String(p.upc ?? '') === q);
    if (exact) {
      addToCart(exact);
      search.value = '';
      results.innerHTML = '';
      search.focus();
    }
  });

  const debounced = debounce(() => {
    const q = search.value.trim();
    if (q.length === 0){ results.innerHTML=''; return; }
    searchProducts(q);
  }, 180);

  search.addEventListener('input', debounced);
  cartDiscountEl.addEventListener('input', () => compute());

  btnPrintCoupon?.addEventListener('click', () => {
    try {
      couponFrame.contentWindow?.focus();
      couponFrame.contentWindow?.print();
    } catch(e) {
      window.print();
    }
  });

  btnFinalize.addEventListener('click', async () => {
    const { discount, total } = compute();
    const items = [...cart.values()].map(it => ({ product_id: it.product.id, qty: it.qty }));
    const payment = payMethodEl.value;

    if (payment === 'DINHEIRO'){
      cashErr.classList.add('d-none');
      cashTotalLabel.textContent = formatBRL(total);
      cashPaid.value = String(total.toFixed(2)).replace('.', ',');
      cashChangeLabel.textContent = formatBRL(0);

      const m = new bootstrap.Modal(cashModalEl);
      m.show();

      const updateChange = () => {
        const paid = Number(String(cashPaid.value).replace(',','.')) || 0;
        const change = Math.max(0, paid - total);
        cashChangeLabel.textContent = formatBRL(change);
      };
      cashPaid.oninput = updateChange;
      updateChange();

      btnCashConfirm.onclick = async () => {
        const paid = Number(String(cashPaid.value).replace(',','.')) || 0;
        if (paid < total){
          cashErr.textContent = 'Valor pago menor que o total.';
          cashErr.classList.remove('d-none');
          return;
        }
        m.hide();
        await finalizeSale({ payment, discount, items, cash_paid: paid, cash_change: paid - total });
      };

      return;
    }

    await finalizeSale({ payment, discount, items });
  });

  async function finalizeSale({ payment, discount, items, cash_paid, cash_change }){
    btnFinalize.disabled = true;
    btnFinalize.textContent = 'Finalizando...';

    const payload = {
      payment_method: payment,
      discount_total: discount,
      items,
    };
    if (payment === 'DINHEIRO'){
      payload.cash_paid = cash_paid;
      payload.cash_change = cash_change;
    }

    const res = await api.send('api/sales.php', 'POST', payload);

    btnFinalize.textContent = 'Finalizar venda';
    btnFinalize.disabled = false;

    if (!res.ok){
      alert(res.error || 'Erro ao finalizar');
      return;
    }

    lastSaleId = res.sale_id;

    cart.clear();
    renderCart(); compute();

    couponFrame.src = `cupom.php?sale_id=${lastSaleId}`;
    new bootstrap.Modal(couponModalEl).show();
  }

  compute();
}

/* ============ HISTÓRICO ============ */
async function initHistorico(){
  const body = document.querySelector('#salesTable tbody');

  async function load(){
    const res = await api.get('api/sales.php?limit=150');
    body.innerHTML = '';
    if (!res.ok) return;

    for (const s of res.data){
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${s.id}</td>
        <td>${escapeHtml(s.created_at)}</td>
        <td>${escapeHtml(s.payment_method)}</td>
        <td>${escapeHtml(s.status)}</td>
        <td class="text-end">${formatBRL(Number(s.discount_total))}</td>
        <td class="text-end">${formatBRL(Number(s.total))}</td>
        <td class="text-end">
          <a class="btn btn-sm btn-outline-light" target="_blank" href="cupom.php?sale_id=${s.id}">Cupom</a>
          <button class="btn btn-sm btn-outline-danger" data-cancel="${s.id}" ${s.status==='CANCELADA'?'disabled':''}>Cancelar</button>
        </td>
      `;
      body.appendChild(tr);

      tr.querySelector('[data-cancel]')?.addEventListener('click', async () => {
        if (!confirm('Cancelar esta venda?')) return;
        const r = await api.send('api/sales.php', 'POST', { action:'cancel', id: s.id });
        if (!r.ok) { alert(r.error || 'Erro'); return; }
        load();
      });
    }
  }

  load();
}

/* ============ RELATÓRIOS (Chart.js) ============ */
function initRelatorios(){
  const fromEl = document.getElementById('r_from');
  const toEl = document.getElementById('r_to');

  const totalSalesEl = document.getElementById('r_total_sales');
  const profitEl = document.getElementById('r_profit');
  const discountEl = document.getElementById('r_discount');

  const chartCanvas = document.getElementById('reportChart');
  const topBody = document.querySelector('#topProfitTable tbody');

  const today = new Date();
  const iso = (d) => d.toISOString().slice(0,10);
  fromEl.value = iso(today);
  toEl.value = iso(today);

  let chartInstance = null;

  async function ensureChartJs(){
    if (window.Chart) return;
    await new Promise((resolve, reject) => {
      const s = document.createElement('script');
      s.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js';
      s.onload = resolve;
      s.onerror = reject;
      document.head.appendChild(s);
    });
  }

  document.getElementById('btnRunReport')?.addEventListener('click', async () => {
    await ensureChartJs();

    const res = await api.get(`api/reports.php?from=${encodeURIComponent(fromEl.value)}&to=${encodeURIComponent(toEl.value)}`);
    if (!res.ok) { alert(res.error || 'Erro'); return; }

    totalSalesEl.textContent = formatBRL(Number(res.data.total_sales));
    profitEl.textContent = formatBRL(Number(res.data.profit_net));
    discountEl.textContent = formatBRL(Number(res.data.total_discount));

    // chart
    const labels = (res.data.series || []).map(x => x.date);
    const salesData = (res.data.series || []).map(x => Number(x.sales_total));
    const profitData = (res.data.series || []).map(x => Number(x.profit_net));

    if (chartInstance) chartInstance.destroy();
    chartInstance = new Chart(chartCanvas, {
      type: 'line',
      data: {
        labels,
        datasets: [
          { label: 'Vendas', data: salesData, borderWidth: 2, tension: 0.25 },
          { label: 'Lucro', data: profitData, borderWidth: 2, tension: 0.25 },
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        }
      }
    });

    // top products
    topBody.innerHTML = '';
    for (const p of (res.data.top_products || [])){
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(p.name)}</td>
        <td>${escapeHtml(p.upc ?? '')}</td>
        <td class="text-end">${formatBRL(Number(p.profit_net))}</td>
      `;
      topBody.appendChild(tr);
    }
  });
}

/* ============ CLIENTS/SUPPLIERS (mantém do seu arquivo atual) ============ */
async function initClients(){ /* ... mantenha a versão que você já tem ... */ }
async function initSuppliers(){ /* ... mantenha a versão que você já tem ... */ }