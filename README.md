# Messenger Integration (Fabric)

Messenger ↔ Minecraft WebSocket Bridge cho Fabric 1.21+

## Tính năng

- **Chat**: Tin nhắn từ Messenger tới Minecraft players
- **Join/Quit**: Thông báo khi player join/leave server
- **Death**: Thông báo khi player chết
- **Advancement**: Thông báo khi player đạt được advancement
- **Status**: `/msstatus` command để check WebSocket connection
- **Server metrics**: TPS, CPU, RAM được gửi mỗi 5 giây
- **Keep-alive**: Ping/pong mỗi 30 giây để giữ kết nối WebSocket

## Yêu cầu

- Java 21
- Minecraft 1.21+
- Fabric Loader 0.15.0+
- Fabric API 0.102.0+

## Cấu hình

Tạo file `config/config.yml`:

```yaml
url: "ws://100.x.x.x:3000"
```

## Build

```bash
./gradlew build
```

JAR file sẽ nằm ở `build/libs/`

## GitHub Actions

Repo đã cấu hình workflow để:
- Build tự động khi push/pull request
- Tạo release tự động với artifact JAR
