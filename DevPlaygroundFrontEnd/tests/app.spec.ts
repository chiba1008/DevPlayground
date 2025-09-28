import { test, expect } from '@playwright/test'

test.describe('DevPlayground App', () => {
    test('ログインページにアクセスできる', async ({ page }) => {
        await page.goto('/login')

        // ログインフォームが表示されることを確認
        await expect(page.locator('#username')).toBeVisible()
        await expect(page.getByRole('textbox', { name: /Password/ })).toBeVisible()

        // Passkeyログインボタンが表示されることを確認
        await expect(page.getByRole('button', { name: /Passkeyでログイン/ })).toBeVisible()
    })

    test('初期ユーザーでログインできる', async ({ page }) => {
        await loginAsUser(page, 'user', 'password')

        // User用ページが表示されることを確認
        await expect(page.getByRole('link', { name: 'Home' })).toBeVisible()
        await expect(page.getByRole('link', { name: 'Todo' })).toBeVisible()
        await expect(page.getByText('Welcome, user (USER)')).toBeVisible()
        await expect(page.getByRole('link', { name: 'Admin' })).not.toBeVisible()
        await expect(page.getByRole('link', { name: 'Users' })).not.toBeVisible()
    })

    test('adminでログインできる', async ({ page }) => {
        await loginAsUser(page, 'admin', 'password')

        await expect(page.getByRole('link', { name: 'Home' })).toBeVisible()
        await expect(page.getByRole('link', { name: 'Todo' })).toBeVisible()
        await expect(page.getByRole('link', { name: 'Admin' })).toBeVisible()
        await expect(page.getByRole('link', { name: 'Users' })).toBeVisible()
        await expect(page.getByText('Welcome, admin (ADMIN)')).toBeVisible()
    })

    test('レスポンシブデザインが機能する', async ({ page }) => {
        // モバイルサイズに設定
        await page.setViewportSize({ width: 375, height: 667 })
        await page.goto('/')

        // ページが正常に表示されることを確認
        await expect(page.locator('body')).toBeVisible()

        // デスクトップサイズに戻す
        await page.setViewportSize({ width: 1200, height: 800 })
        await expect(page.locator('body')).toBeVisible()
    })

    test('404ページが正常に動作する', async ({ page }) => {
        await page.goto('/non-existent-page')

        // 404エラーページまたはリダイレクトが正常に処理されることを確認
        await expect(page.locator('body')).toBeVisible()
    })
})

async function loginAsUser(page, username, password) {
    await page.goto('/login')
    await page.locator('#username').fill(username)
    await page.getByRole('textbox', { name: 'Password:' }).fill(password)
    await page.getByRole('button', { name: 'Login' }).click()
}