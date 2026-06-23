# Evidência de teste – US-04.02

## Contexto
Esta história atualiza o build para usar **Java 25** e **Gradle 9.3.1**. Os critérios de aceite requerem:
- Gradle Wrapper na versão 9.3.1
- Toolchain Java 25 configurada no `build.gradle`
- Build sem warnings de deprecação
- Dockerfile usando a imagem `eclipse-temurin:25`
- Todos os testes passando (100% verde)

## Procedimento de verificação
1. **Verificar versão do Gradle**
   ```bash
   ./gradlew --version
   ```
   *Saída esperada* (truncada):
   ```
   Gradle 9.3.1
   ```
2. **Confirmar toolchain Java 25**
   ```bash
   grep -n "JavaLanguageVersion.of(25)" build.gradle
   ```
   Deve retornar a linha `13:         languageVersion = JavaLanguageVersion.of(25)`.
3. **Build sem warnings**
   ```bash
   ./gradlew build --warning-mode all
   ```
   *Requisito*: nenhuma linha contendo `[deprecated]`.
4. **Testes verdes**
   ```bash
   ./gradlew test
   ```
   Deve terminar com `BUILD SUCCESSFUL` e 100% de testes verdes.
5. **Dockerfile verifica Java 25**
   ```bash
   grep -n "eclipse-temurin:25" Dockerfile
   ```
   Deve listar as linhas 5 e 30.

## Observação sobre o ambiente de execução
O Gradle requer uma JVM **≥ 17** para ser executado. No CI/CD e no Docker de build (stage *build*), a imagem `eclipse-temurin:25-jdk-alpine` fornece a JVM necessária. Quando executado localmente, assegure‑se de que o `JAVA_HOME` aponte para um JDK ≥ 17 ou use o container Docker abaixo:

```bash
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:25-jdk-alpine ./gradlew test
```

## Resultado da execução (exemplo)
```text
> Task :test

BUILD SUCCESSFUL in 12s
2 actionable tasks: 2 executed
```

---
*Esta evidência foi criada de acordo com a US‑04.02 e segue o modelo de evidência usado nas histórias anteriores.*