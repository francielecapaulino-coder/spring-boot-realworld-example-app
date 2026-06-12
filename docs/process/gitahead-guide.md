# Guia de instalação e uso do GitAhead

> **Repositório:** `francielecapaulino-coder/spring-boot-realworld-example-app`
> **Projeto:** RealWorld Platform Modernization
> **Versão:** 1.0 · Junho 2026
> **GitAhead v2.7.1 · https://github.com/gitahead/gitahead**

---

## 1. O que é o GitAhead

O **GitAhead** é um cliente Git gráfico, leve e de código aberto (licença MIT), que exibe o histórico do repositório como um **grafo de commits** — facilitando a leitura de branches, merges e mensagens de commit sem depender da linha de comando.

Neste projeto, o GitAhead tem 3 finalidades:

1. **Validar commits no `bleeding`** após rodar `scripts/bleeding-commit.sh`.
2. **Acompanhar o progresso paralelo** da Dupla A e da Dupla B.
3. **Auditar Conventional Commits** antes de abrir a PR.

> ⚠️ **Status de manutenção:** o GitAhead **não está mais em desenvolvimento ativo**. Lançamentos de manutenção serão feitos conforme necessário, mas nenhuma nova feature está planejada. Para Linux ou para um fork ativamente mantido, use o **Gittyup** (ver Seção 3).

| Atributo | Valor |
|---|---|
| **Versão** | v2.7.1 (dezembro 2023) |
| **Repositório** | https://github.com/gitahead/gitahead |
| **Licença** | MIT |
| **macOS** | Big Sur (11) ou posterior — binário universal (Intel + Apple Silicon) |
| **Windows** | Windows 10+, **64-bit apenas** |
| **Linux** | Último binário oficial: v2.6.3 (v2.7.x não tem binário) |

### Por que GitAhead e não outro cliente

| Cliente | Por que não nesta escolha |
|---|---|
| **SourceTree** | Proprietário (Atlassian), pesado, sem Linux |
| **GitHub Desktop** | Não exibe grafo completo de branches; foco em fluxo simplificado |
| **GitKraken** | Recursos avançados atrás de licença paga; mais pesado |
| **GitAhead** | Leve, MIT, grafo de commits claro — ideal para validar o `bleeding` |

---

## 2. Instalação

### macOS (v2.7.1)

**Opção A — Download direto**

URL: https://github.com/gitahead/gitahead/releases/download/v2.7.1/GitAhead-2.7.1.dmg

1. Baixe o `.dmg` pela URL acima.
2. Abra o arquivo `.dmg`.
3. Arraste **GitAhead** para a pasta **Applications**.
4. Na primeira abertura, o macOS pode mostrar aviso de segurança — escolha **"Open Anyway"** em Ajustes do Sistema > Privacidade e Segurança.
5. Abra o GitAhead pela pasta Applications.

**Opção B — Homebrew**

```bash
brew install --cask gitahead
# instala a v2.7.1
```

**Verificação**

```bash
open /Applications/GitAhead.app
```

- **Apple Silicon (M1/M2/M3):** o binário é **universal** — funciona nativamente, sem Rosetta.
- **macOS Sequoia / Sonoma:** se o botão "Open Anyway" não aparecer, remova a quarentena:

```bash
sudo xattr -cr /Applications/GitAhead.app
```

### Windows (v2.7.1)

URL: https://github.com/gitahead/gitahead/releases/download/v2.7.1/GitAhead-win64-2.7.1.exe

1. Baixe o `.exe` 64-bit pela URL acima.
2. Execute o instalador `.exe`.
3. Siga o assistente de instalação (wizard).
4. Abra o GitAhead pelo Menu Iniciar.

**Requisitos:** Windows 10+, **64-bit** (32-bit **não** é suportado).

### Linux (v2.6.3 — última versão com binário)

> ⚠️ **As versões v2.7.0 e v2.7.1 NÃO têm binário pré-compilado para Linux.** Use a v2.6.3 (abaixo) ou o Gittyup (recomendado).

URL: https://github.com/gitahead/gitahead/releases/download/v2.6.3/GitAhead-2.6.3-linux.sh

```bash
chmod +x GitAhead-2.6.3-linux.sh
./GitAhead-2.6.3-linux.sh
# responda "yes" quando solicitado
```

### Alternativa: Gittyup (Linux + desenvolvimento ativo)

URL: https://github.com/Murmele/Gittyup

O **Gittyup** é um **fork ativamente mantido** do GitAhead, com interface praticamente idêntica e disponível para **Linux, macOS e Windows**.

> O guia de uso abaixo se aplica igualmente ao Gittyup.

---

## 3. Configuração inicial

1. Abra o GitAhead.
2. Menu **File > Open Repository**.
3. Navegue até a pasta do seu fork local (`spring-boot-realworld-example-app`).
4. Clique em **Open**.
5. O grafo de commits aparece no painel central.

**Verificação:** Menu **View > Remotes** deve mostrar:

```text
origin → https://github.com/francielecapaulino-coder/spring-boot-realworld-example-app
```

---

## 4. Uso no workflow

### 4.1 Visualizar o branch `bleeding`

1. No painel esquerdo, expanda **Remote Branches**.
2. Localize `origin/bleeding`.
3. Clique para exibir o grafo do `bleeding`.

Formato esperado de cada commit no `bleeding`:

```text
tipo(escopo): step X/Y - descrição

refs #XX
bleeding-branch-auto-commit: 2026-06-11T16:40:00Z
source-branch: tipo/us-XX.XX-descricao-curta
```

### 4.2 Validar commit após `./scripts/bleeding-commit.sh`

1. Pressione **F5** para atualizar.
2. Selecione `origin/bleeding`.
3. Verifique no commit mais recente:
   - ✓ Mensagem no formato `tipo(escopo): step X/Y - ...`
   - ✓ Linha `refs #XX` (ou `closes #XX`)
   - ✓ Linha `bleeding-branch-auto-commit: <timestamp>`
   - ✓ Linha `source-branch: <sua-branch>`
   - ✓ O commit reflete exatamente a etapa que você acabou de rodar

**Se o commit não aparecer:**

```bash
git log origin/bleeding -1
# se o commit existe no git mas não no GitAhead: feche e reabra o repositório no GitAhead
```

### 4.3 Acompanhar branches das duplas em paralelo

1. No painel esquerdo, marque os checkboxes de múltiplas branches para exibi-las juntas no grafo.
2. Compare o avanço de cada dupla lado a lado.

```text
master ──●──●──────────────●  (integração)
          \                /
bleeding   ●──●──●──●──●──●    (rastreamento contínuo)
              \
feat/Dupla-A   ●──●──●         (história X)
                  \
test/Dupla-B       ●──●        (história Y)
```

- **Ctrl+Click** em dois commits para comparar as diferenças entre eles.

### 4.4 Auditar Conventional Commits antes da PR

1. Selecione a sua **feature branch** no painel esquerdo.
2. Para cada commit, verifique:
   - ✓ Tipo válido (`feat`, `fix`, `chore`, `docs`, `test`, `refactor`, `ci`, `perf`, `style`)
   - ✓ Descrição em lowercase, sem ponto final
   - ✓ Referência à issue (`closes #XX` ou `refs #XX`)

**Se algum commit estiver fora do padrão:**

```bash
git rebase -i <commit-anterior>
# edite as mensagens fora do padrão antes de abrir a PR
```

### 4.5 Verificar branch restaurada após o script

1. No GitAhead, o **HEAD** é indicado pelo marcador na branch atual.
2. Confirme que o HEAD aponta para a sua **branch de trabalho** — não para `bleeding`.

**Se o HEAD estiver no `bleeding`:**

```bash
git checkout <sua-branch-de-trabalho>
```

---

## 5. Atalhos úteis

| Atalho | Ação |
|---|---|
| `F5` | Atualizar (refresh) o grafo |
| `Ctrl+O` | Abrir repositório |
| Clique | Selecionar um commit e ver detalhes |
| `Ctrl+Click` | Selecionar dois commits para comparar |
| `Ctrl+F` | Buscar no histórico |
| `Ctrl+L` | Focar no campo de log/busca |
| Checkbox | Exibir/ocultar uma branch no grafo |
| `Ctrl+Enter` | Confirmar/commit no painel de edição |

---

## 6. Troubleshooting

1. **Repositório não aparece** → Menu **File > Open Repository** e selecione a pasta do fork.
2. **Branch `bleeding` ausente** → Pressione **F5** e expanda **Remote Branches**.
3. **Commits não aparecem após o script** → **F5** → se persistir, feche e reabra o repositório; diagnostique com `git log origin/bleeding -1`.
4. **macOS — aviso de segurança** → **"Open Anyway"** em Privacidade e Segurança; no Sequoia/Sonoma rode `sudo xattr -cr /Applications/GitAhead.app`.
5. **Linux — v2.7.1 sem binário** → use a v2.6.3 (`GitAhead-2.6.3-linux.sh`) **ou** o Gittyup (https://github.com/Murmele/Gittyup).

---

## 7. Checklist DoR

```text
[ ] GitAhead (ou Gittyup) instalado e abre sem erro
[ ] Repositório francielecapaulino-coder adicionado
[ ] origin aponta para a URL real do fork
[ ] Branch bleeding visível em "Remote Branches"
[ ] Commit mais recente mostra mensagem com step X/Y
[ ] HEAD aponta para a branch de trabalho (não para bleeding)
[ ] F5 atualiza sem erro
```

---

## 8. Atualizações deste guia

Atualize este documento **via Pull Request** com commit do tipo `docs`:

```bash
git commit -m "docs(process): update gitahead guide — <motivo>"
```

| Versão | Data | O que mudou |
|---|---|---|
| 1.0 | Junho 2026 | Versão inicial — instalação (macOS/Windows/Linux), Gittyup, uso no workflow, troubleshooting, checklist DoR |

---

> **GitAhead v2.7.1:** https://github.com/gitahead/gitahead/releases/tag/v2.7.1 (MIT)
> **Alternativa Linux (mantida):** Gittyup — https://github.com/Murmele/Gittyup
> Veja também: [`harness-development.md`](./harness-development.md) · [`definition-of-ready.md`](./definition-of-ready.md) · [`scripts/bleeding-commit.sh`](../../scripts/bleeding-commit.sh)
