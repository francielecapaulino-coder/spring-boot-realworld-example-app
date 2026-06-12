# Guia de uso do Coda — Vibe Coding Log

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Projeto:** RealWorld Platform Modernization
> **Versão:** 1.0 · Junho 2026

> ℹ️ **Pendência:** a URL real do workspace Coda (Parte A, manual) ainda não foi inserida.
> Os marcadores `TODO-CODA-URL` abaixo devem ser substituídos pela URL real assim que disponível.

---

## 1. Workspace do projeto

[RealWorld Platform Modernization — Vibe Coding Log](TODO-CODA-URL)

Documento central onde cada história e cada etapa de vibe coding é registrada para rastreabilidade total.

---

## 2. Por que o Coda é obrigatório

Premissa da gestão: **"Code and commits with Coda"**.

O Coda responde a 4 perguntas que o código sozinho não responde:

1. **Qual prompt** gerou este trecho de código?
2. **Qual modelo/ferramenta** foi usado e com qual contexto?
3. **Quais ajustes** humanos foram necessários sobre o output da IA?
4. **Quais skills** foram aprendidas/aplicadas em cada etapa?

> Sem Coda = caixa preta; Com Coda = processo rastreável.

---

## 3. Quando usar o Coda

| Momento | Ação no Coda |
|---|---|
| Antes de iniciar a história | Abrir o épico, localizar a história, mudar status para "Em andamento" |
| Antes de cada etapa | Adicionar uma linha na sub-tabela de Etapas |
| Durante o vibe coding | Preencher modelo, output, skills e ajustes |
| Após cada etapa | Colar o link do commit `bleeding` e marcar status "Concluída" |
| Antes de abrir a PR | Copiar o link da história no Coda |
| Ao fechar a história | Status "Concluída" + link da PR |

---

## 4. Como documentar uma etapa

### Estrutura de cada etapa (11 campos)

```text
Etapa: 2/5
Descrição: Implementar calculateReadingTime no ArticleService
Prompt: (ver abaixo)
Modelo/Tool: Claude Sonnet 4.6
Output resumido: Método com MAX(1, CEIL(wordCount/200))
Commit bleeding: https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app/commit/abc1234
Commit feature: abc1234 feat(articles): add calculateReadingTime method
Skills: MAX() e CEIL() em Java; edge case body vazio
Ajustes: Nenhum — output correto
Tempo: 12 min
Status: ✅ Concluída
```

### Formato obrigatório do prompt

```text
Etapa 2/5 da história US-13.02 (tempo estimado de leitura).
Repositório: francielecapaulino-coder/spring-boot-realworld-example-app
Branch: feat/us-13.02-reading-time

Contexto:
- ADR-005 (Node interface e records) e camada `application`.
- Fórmula: reading_time = MAX(1, CEIL(wordCount / 200)).

O que preciso:
- Método `int calculateReadingTime(String body)` no ArticleService.

Restrições:
- Apenas o método nesta etapa. Sem testes (virão na etapa 3/5).
```

---

## 5. Navegando no workspace

### Estrutura de páginas

```text
RealWorld Platform Modernization — Vibe Coding Log
├── Visão Geral (13 épicos)
├── EPIC-01 — Fundação do processo
├── EPIC-02 — Segurança
├── EPIC-03 — Containerização
├── EPIC-04 — Modernização do runtime
├── EPIC-05 — Migração de persistência
├── EPIC-06 — Record types
├── EPIC-07 — Pitest
├── EPIC-08 — Testes de contrato
├── EPIC-09 — Playwright
├── EPIC-10 — Observabilidade
├── EPIC-11 — OpenAPI
├── EPIC-12 — Soft delete
└── EPIC-13 — Tempo de leitura
```

### Encontrando sua história

1. Abra o documento [Vibe Coding Log](TODO-CODA-URL).
2. Identifique o épico da sua história (ex: EPIC-13).
3. Abra a página do épico no painel lateral.
4. Localize a linha da história na Tabela de Histórias.
5. Expanda a sub-tabela de Etapas dessa história.

---

## 6. Campos obrigatórios para a PR

- [ ] Link da história no Coda copiado
- [ ] Todas as etapas com status "Concluída"
- [ ] Cada etapa com prompt completo registrado
- [ ] Cada etapa com commit `bleeding` vinculado
- [ ] Skills e ajustes preenchidos honestamente

---

## 7. Gerando o link para a PR

1. Localize a linha da sua história na Tabela de Histórias.
2. Clique nos três pontos (`⋯`) da linha.
3. Selecione **"Copy link to row"** e cole no campo Coda da PR.

---

## 8. Regras do Coda

1. **Documentar durante, não depois** — registre cada etapa enquanto a executa.
2. **Prompt completo, não resumo** — cole o prompt inteiro, não uma paráfrase.
3. **Um registro por etapa** — uma linha na sub-tabela por etapa verificável.
4. **Skills honestas** — incluir os ajustes humanos necessários sobre o output da IA.
5. **Link obrigatório na PR** — a PM Franciele não aprova PR sem o link do Coda.

---

## 9. Dúvidas frequentes

**Posso escrever em português?**
Sim. Todo o registro pode ser em português.

**E se o prompt for muito longo?**
Documente completo mesmo assim — o valor está no prompt íntegro.

**Esqueci de registrar uma etapa. E agora?**
Registre retroativamente — é melhor do que não registrar.

**E se eu não usei IA nesta etapa?**
Preencha o campo Modelo/Tool com **"Manual"**.

---

> **Workspace:** [RealWorld Platform Modernization — Vibe Coding Log](TODO-CODA-URL)
> **Premissa da gestão:** "Code and commits with Coda".
> **Gate do DoD:** link do Coda obrigatório — ver [`definition-of-done.md`](./definition-of-done.md).
