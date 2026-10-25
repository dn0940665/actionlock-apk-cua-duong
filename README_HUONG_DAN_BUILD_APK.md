# ActionLock Free (AL-01) — Mã Nguồn Ứng Dụng Android (.APK) Mở Khóa 100% Premium

Dự án này là phiên bản hoàn toàn miễn phí (không quảng cáo, không thu phí thuê bao $8.99/tháng hay $24.99/năm) mô phỏng đầy đủ mọi tính năng của **ActionLock: App Blocker (`com.pushupunlock`)**:
1. **MOD-01 — Smart App Blocker (`AppBlockerAccessibilityService.kt`)**: Chặn trực tiếp ở cấp hệ điều hành Android khi mở TikTok, Instagram, YouTube, Facebook, X (Twitter), Threads, Liên Quân... và bật màn hình thử thách vận động.
2. **MOD-02 — On-Device AI Rep Counting (`PoseRepDetector.kt`)**: Đếm nhịp tự động không cần chạm màn hình cho 4 bài tập chuẩn ActionLock: **Hít đất (Push-ups), Gánh đùi (Squats), Gập bụng (Sit-ups) và Chùng chân (Lunges)** bằng Google ML Kit Pose Detection & Camera WebRTC.
3. **MOD-03 — Screen-Time Economy**: Mở khóa linh hoạt **15, 30, 60 hoặc 120 phút** sau mỗi hiệp tập + Quỹ Thời Gian (Time Bank).
4. **MOD-04 & MOD-05 — Life RPG, Levels, Ranks, Daily & Weekly Quests, Hero Skins**: Mở khóa toàn bộ 6 trang phục Anh Hùng bằng Xu tập luyện miễn phí.
5. **MOD-06 — Detailed Stats & On-Device Privacy**: Xử lý 100% trên thiết bị, không tải video lên máy chủ.

---

## CÁCH 1: Tự động xuất file `.APK` trên điện thoại/máy tính qua GitHub Actions (Không cần cài Android Studio)
1. Giải nén file `actionlock_android_source.zip` này.
2. Tạo một kho chứa (Repository) mới trên [GitHub](https://github.com) và tải toàn bộ các file trong thư mục này lên.
3. Vào tab **Actions** trên GitHub ➔ Workflow **Build ActionLock Free APK** sẽ tự động chạy (nhờ file `.github/workflows/build-apk.yml` đã cấu hình sẵn).
4. Sau khoảng 2 phút, nhấn vào bản dựng hoàn tất ➔ Tải file **`ActionLock-Free-v1.0-debug.apk`** trong mục **Artifacts** về điện thoại Android và cài đặt!

## CÁCH 2: Biên dịch trực tiếp bằng Android Studio
1. Mở **Android Studio** ➔ Chọn **Open** và trỏ tới thư mục `actionlock_android_source`.
2. Đợi Gradle đồng bộ xong, chọn menu **Build ➔ Build Bundle(s) / APK(s) ➔ Build APK(s)** (hoặc chạy lệnh `./gradlew assembleDebug`).
3. File APK sẽ nằm tại: `app/build/outputs/apk/debug/app-debug.apk`.
4. Sau khi cài trên máy Android, vào **Cài đặt (Settings) ➔ Hỗ trợ tiếp cận (Accessibility) ➔ ActionLock Free Blocker** và bật dịch vụ để ứng dụng tự động chặn TikTok/Instagram/YouTube khi hết giờ!
