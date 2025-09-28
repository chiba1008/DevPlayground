# vue-project

This template should help get you started developing with Vue 3 in Vite.

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
npm run test:unit
```

### Run End-to-End Tests with [Playwright](https://playwright.dev)

```sh
# Install browsers for the first run
npx playwright install

# When testing on CI, must build the project first
npm run build

# Runs the end-to-end tests
npm run test:e2e
# Runs the tests only on Chromium
npm run test:e2e -- --project=chromium
# Runs the tests of a specific file
npm run test:e2e -- tests/example.spec.ts
# Runs the tests in debug mode
npm run test:e2e -- --debug
```
### Playwrightテストジェネレーター

```sh
# ブラウザでの操作を記録してテストを生成
npx playwright codegen localhost:5173
```

## Playwrightのセットアップと設定

### 初期セットアップ

1. **Playwrightのインストール**: プロジェクトの依存関係に既に含まれています
2. **ブラウザのインストール**: ブラウザバイナリをインストールするため、一度実行してください
   ```sh
   npx playwright install
   ```

### テストの作成

Playwrightテストは`e2e/`ディレクトリに配置されます。テストの作成と実行方法：

#### 新しいテストの作成

```typescript
// e2e/example.spec.ts
import { test, expect } from '@playwright/test';

test('基本的なテストの例', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/Vue App/);
});
```

#### テストジェネレーター（Codegen）

Playwrightのcodegenツールを使用して、ブラウザでの操作を記録してテストを生成：

```sh
# まず開発サーバーを開始
npm run dev

# 別のターミナルでcodegenを開始
npx playwright codegen localhost:5173
```

これによりブラウザが開き、アプリケーションと対話すると、Playwrightが自動的にテストコードを生成します。

#### テストの実行

```sh
# すべてのe2eテストを実行
npm run test:e2e

# ヘッドモードでテストを実行（ブラウザを表示）
npm run test:e2e -- --headed

# 特定のブラウザでテストを実行
npm run test:e2e -- --project=chromium
npm run test:e2e -- --project=firefox
npm run test:e2e -- --project=webkit

# 特定のテストファイルを実行
npm run test:e2e -- e2e/example.spec.ts

# デバッグモード（テストをステップ実行）
npm run test:e2e -- --debug
```

#### テストレポート

テスト実行後、HTMLレポートを表示：

```sh
npx playwright show-report
```

### Playwrightの設定

Playwrightの設定は`playwright.config.ts`で定義されています。主要な設定項目：

- **baseURL**: 開発サーバーと連携するよう自動設定
- **ブラウザ**: Chromium、Firefox、WebKit
- **スクリーンショット**: 失敗時に撮影（`only-on-failure`）
- **ビデオ**: 失敗時に保存（`retain-on-failure`）
- **トレース**: 最初の再試行時に収集

### ベストプラクティス: [Playwright公式ドキュメント](https://playwright.dev/)

#### Locatorを使用する
```typescript
// 👎
page.locator('button.buttonIcon.episode-actions-later');

// 👍
page.getByRole('button', { name: 'submit' });
page.getByLabel('Username');
```

#### Test Generatorを活用する
```sh
npx playwright codegen localhost:5173/login
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```
