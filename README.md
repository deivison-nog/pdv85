# PDV Info85

Rodar no XAMPP (Windows):

- Pasta: `C:\xampp\htdocs\pdv-info85\`
- URL: `http://localhost/pdv-info85/`
- Banco: `pdv85` (MySQL)

## Instalação
1. No phpMyAdmin, importe: `database/schema.sql`
2. Acesse: `http://localhost/pdv-info85/`

## Login padrão
- Usuário: **administrador**
- Senha: **admin123**

> O usuário é criado automaticamente na primeira execução (seed via PHP com `password_hash`).

## Módulos
- PDV / Caixa (UPC, dinheiro com troco, cupom em modal)
- Produtos (custo, venda e % de ganho)
- Clientes (dívida do cliente)
- Fornecedores (dívida para fornecedor)
- Relatórios (total de vendas e lucro líquido por período)