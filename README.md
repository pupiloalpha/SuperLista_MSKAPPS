# Super Lista

Aplicativo Android para criar, organizar e compartilhar listas — como listas de compras, presentes, material escolar, tarefas e prioridades.

## Funcionalidades

- Criar, editar, renomear, copiar e excluir listas.
- Adicionar itens com descrição, quantidade, unidade e valor.
- Marcar itens como colocados na cesta e acompanhar o valor previsto e o total a pagar.
- Compartilhar uma lista por meio dos aplicativos disponíveis no dispositivo, como e-mail e mensagens.
- Criar lembretes no calendário do Android.
- Fazer backup e restaurar as listas pelas configurações do aplicativo.

As listas são armazenadas localmente em um banco de dados SQLite no dispositivo.

## Requisitos

- Android Studio com suporte a projetos Android baseados em Gradle.
- JDK compatível com Java 8.
- Android SDK 29 (compile/target); o aplicativo declara suporte a partir do Android 4.0.3 (API 15).

O módulo Android está em `app/`. O arquivo `app/build.gradle` configura `com.msk.superlista` e as dependências AndroidX e Material usadas pelo projeto.

## Como abrir e executar

1. Clone ou baixe este repositório.
2. Abra a pasta do projeto no Android Studio.
3. Aguarde a sincronização do Gradle e instale os componentes do Android SDK solicitados, incluindo o SDK 29.
4. Selecione a configuração `app` e execute em um emulador ou dispositivo Android.

> **Observação:** o repositório contém a configuração Gradle do módulo `app`, mas não inclui o wrapper Gradle nem os arquivos Gradle de nível raiz. Caso o Android Studio não consiga sincronizar o projeto diretamente, será necessário completar essa configuração usando uma versão do Android Gradle Plugin compatível com o projeto legado.

## Estrutura do projeto

```text
app/
├── build.gradle                 # Configuração do módulo Android
└── src/main/
    ├── AndroidManifest.xml      # Componentes e permissões do aplicativo
    ├── java/com/msk/superlista/
    │   ├── SuperLista.java      # Tela principal e gerenciamento das listas
    │   ├── db/                  # Persistência SQLite e edição de itens
    │   ├── info/                # Configurações, backup, ajuda e sobre
    │   └── listas/              # Exibição e uso das listas e da cesta
    └── res/                     # Layouts, menus, ícones e textos
```

## Tecnologias

- Java 8
- Android SDK
- SQLite
- AndroidX, Material Components e ConstraintLayout

## Desenvolvedor

Paulo E. S. Mesquita
