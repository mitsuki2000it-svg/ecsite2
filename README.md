# ECサイト

Java / Spring Bootで作成した学習用ECサイトです。

## 主な機能

- 商品追加 ・ 編集 ・ 削除
- 商品検索
-　価格範囲検索
- 在庫あり絞り込み
- ソート
- ページネーション
- カート購入
- ユーザー登録
- ログイン　/ ログアウト
- USER / ADMIN 権限管理
- 購入履歴
- 注文履歴
- 発送処理
- キャンセル処理

## 使用技術

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- H2
- JUnit
- Mockito
- HTML
- CSS
- JavaScript

## テスト

- JUnitを使った単体テスト
- Mopckitoを使ったRepositoryのMock
- 正常系 / 異常系テスト
- ArgumentCaptorを使った引数確認
- H2を使った総合テスト
- @Transactionalのロールバック確認

## 工夫した点

- カート購入時に複数商品を1つの注文として管理
- 在庫更新時に悲観ロックを使用
- @Transactionalで購入処理の整合性を管理
- キャンセル時に購入数分の在庫を戻す
- USER / ADMINで権限を分離
- CSRFを有効にしたままPOST / PUT / DELETEを実装
- 購入時点の商品名　・　価格をSaleへ保存
- 単体テストと統合テストの両方を実装

## 開発環境

- Java 17
- Spring Boot
- Eclips
- VS code
- MySQL

## 起動方法

1. MySQLで `product_db` データベースを作成する 

2. `application.properties`　にMySQL接続情報を設定する

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/product_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

3. Spring Bootアプリケーションを起動する

Eclipseから `Ecsite2Application.java` を実行する。

4. ブラウザで以下にアクセスする

http://localhost:8080

## 今後追加したい機能

- 配送先住所の登録
- 決済機能
- 本番環境へのデプロイ