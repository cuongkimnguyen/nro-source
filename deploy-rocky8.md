# Rocky Linux 8.8 full setup guide (Java 17)

Tài liệu này hướng dẫn đầy đủ 2 cách triển khai:
- **Cách A:** chạy trực tiếp trên Rocky 8.8 bằng Java 17 + systemd
- **Cách B:** chạy bằng Docker Engine + Docker Compose

---

## 0) Chuẩn bị trước khi deploy

Bạn cần xác định trước:
- IP public hoặc domain của server
- Port game sẽ mở cho client, mặc định: `14445`
- Port web/Spring Boot, mặc định: `1707`
- Có dùng DB cùng máy hay DB ngoài
- Các file runtime mà game cần ngoài source code

### Các thư mục runtime rất có thể bạn phải tự bổ sung
Archive gốc bạn gửi **không thấy đầy đủ** các thư mục dữ liệu runtime như:
- `resources/data`
- `resources/image`
- `resources/map/block`
- hoặc các file map/data/custom assets khác

Nếu game server của bạn đang dùng các file này trên Windows, bạn phải copy chúng sang Rocky Linux trước khi chạy.

---

## Cách A — chạy trực tiếp trên Rocky 8.8

## 1) Update hệ thống
```bash
sudo dnf update -y
sudo reboot
```
Sau reboot, SSH lại vào máy.

## 2) Cài Java 17, công cụ build, và tiện ích cần thiết
```bash
sudo dnf install -y \
  java-17-openjdk \
  java-17-openjdk-devel \
  git unzip tar wget curl vim which procps-ng policycoreutils-python-utils
```

Kiểm tra:
```bash
java -version
javac -version
```
Kỳ vọng thấy Java 17.

## 3) Nếu dùng DB local thì cài MySQL 8 hoặc MariaDB
### Phương án dễ nhất: dùng MySQL bằng Docker
Cách này ổn định và ít lệch version hơn, đặc biệt nếu source đang viết cho MySQL 8.

### Nếu bạn vẫn muốn DB native trên Rocky
MariaDB có thể chạy được, nhưng vì project đang dùng `MySQL8Dialect`, an toàn nhất là dùng MySQL 8.

---

## 4) Tạo user chạy service
```bash
sudo useradd -r -m -d /opt/ngocrong -s /sbin/nologin ngocrong || true
sudo mkdir -p /opt/ngocrong
sudo chown -R ngocrong:ngocrong /opt/ngocrong
```

## 5) Copy project lên server
Ví dụ từ máy local:
```bash
scp -r game_server_rocky88 user@YOUR_SERVER_IP:/tmp/
```

Trên Rocky:
```bash
sudo rsync -av /tmp/game_server_rocky88/ /opt/ngocrong/
sudo chown -R ngocrong:ngocrong /opt/ngocrong
cd /opt/ngocrong
```

## 6) Tạo các thư mục runtime
```bash
mkdir -p logs log backup backup_sql runtime-data
mkdir -p resources/data resources/image resources/map/block
```

Nếu bạn có data game thật từ máy Windows, copy vào đúng các thư mục tương ứng.

## 7) Cấu hình ứng dụng
Sửa file:
```bash
vim /opt/ngocrong/src/main/resources/application.properties
```

Các giá trị tối thiểu cần đúng:
```properties
server.host=0.0.0.0
server.port_game=14445
server.port=1707

game.servers=Server 1:YOUR_PUBLIC_IP:14445:0,Server 2:YOUR_PUBLIC_IP:14445:0,0,0

database.host=127.0.0.1
database.port=3306
database.name=hunr_2026
database.user=root
database.password=YOUR_DB_PASSWORD

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/hunr_2026?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Bangkok
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD
```

Nếu DB ở máy khác, thay `127.0.0.1` bằng IP/hostname thật.

## 8) Build project
```bash
cd /opt/ngocrong
chmod +x mvnw build.sh run.sh autorun.sh backupsql.sh
./build.sh
```

Jar sau build sẽ là:
```bash
target/ngocrongonline-0.0.1-SNAPSHOT.jar
```

## 9) Chạy thử bằng tay
```bash
cd /opt/ngocrong
XMS=1G XMX=4G ./run.sh
```

Xem log ở shell khác:
```bash
tail -f /opt/ngocrong/logs/server.out
tail -f /opt/ngocrong/logs/error.log
```

Nếu start thành công, dừng bằng `Ctrl+C` rồi cấu hình systemd.

## 10) Tạo systemd service
```bash
sudo cp /opt/ngocrong/ngocrong.service /etc/systemd/system/ngocrong.service
sudo systemctl daemon-reload
sudo systemctl enable ngocrong
sudo systemctl start ngocrong
sudo systemctl status ngocrong --no-pager -l
```

Follow log:
```bash
journalctl -u ngocrong -f
```

## 11) Mở firewall
```bash
sudo firewall-cmd --permanent --add-port=14445/tcp
sudo firewall-cmd --permanent --add-port=1707/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports
```

## 12) Nếu SELinux gây chặn port/app
Kiểm tra trạng thái:
```bash
getenforce
```

Nếu là `Enforcing`, trước tiên test nhanh bằng log chứ chưa tắt SELinux ngay.
Nếu cần cho Java bind port tùy chỉnh, có thể thêm port:
```bash
sudo semanage port -a -t http_port_t -p tcp 1707 || true
```

Port game `14445` không phải HTTP port; nếu bị SELinux chặn kết nối đặc thù thì nên đọc audit log:
```bash
sudo ausearch -m avc -ts recent
```

## 13) Kiểm tra port
```bash
ss -ltnp | grep -E '14445|1707'
```

## 14) Các lệnh vận hành thường dùng
```bash
sudo systemctl restart ngocrong
sudo systemctl stop ngocrong
sudo systemctl start ngocrong
sudo systemctl status ngocrong
journalctl -u ngocrong -n 200 --no-pager
```

---

## Cách B — chạy bằng Docker trên Rocky 8.8

Cách này gọn hơn cho production nhỏ hoặc staging.

## 1) Cài Docker Engine
```bash
sudo dnf install -y dnf-plugins-core
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

Sau đó logout/login lại, hoặc chạy:
```bash
newgrp docker
```

Kiểm tra:
```bash
docker --version
docker compose version
```

## 2) Copy project vào máy
Ví dụ:
```bash
sudo mkdir -p /opt/ngocrong
sudo chown -R $USER:$USER /opt/ngocrong
rsync -av game_server_rocky88/ /opt/ngocrong/
cd /opt/ngocrong
```

## 3) Tạo file `.env`
```bash
cp .env.example .env
vim .env
```

Các biến tối thiểu phải sửa:
```env
MYSQL_ROOT_PASSWORD=REPLACE_ME
DATABASE_PASSWORD=REPLACE_ME
SPRING_DATASOURCE_PASSWORD=REPLACE_ME
GAME_PUBLIC_HOST=YOUR_PUBLIC_IP
GAME_SERVERS=Server 1:YOUR_PUBLIC_IP:14445:0,Server 2:YOUR_PUBLIC_IP:14445:0,0,0
```

Nếu chỉ có 1 server game, bạn vẫn có thể để format cũ nếu code client của bạn đang parse như vậy.

## 4) Build và start
```bash
docker compose up -d --build
```

## 5) Xem log
```bash
docker compose logs -f app
docker compose logs -f db
```

## 6) Kiểm tra container
```bash
docker ps
docker compose ps
```

## 7) Stop / start / restart
```bash
docker compose stop
docker compose start
docker compose restart
```

## 8) Tắt hẳn stack
```bash
docker compose down
```

## 9) Nếu muốn xóa cả DB volume
```bash
docker compose down -v
```
Cẩn thận vì lệnh này xóa dữ liệu database trong volume.

---

## Kết nối DB ngoài thay vì DB trong compose

Nếu bạn đã có MySQL ngoài container, sửa `.env`:
```env
DATABASE_HOST=YOUR_DB_HOST
DATABASE_PORT=3306
DATABASE_NAME=hunr_2026
DATABASE_USER=root
DATABASE_PASSWORD=YOUR_DB_PASSWORD
SPRING_DATASOURCE_URL=jdbc:mysql://YOUR_DB_HOST:3306/hunr_2026?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Bangkok
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
```

Sau đó bạn có thể vẫn dùng compose cho `app`, hoặc bỏ hẳn service `db` nếu muốn.

---

## Import SQL ban đầu

Nếu bạn có file SQL khởi tạo trong thư mục `sql/`, compose sẽ mount nó vào MySQL init directory lần đầu tạo DB volume.

Nếu DB đã tồn tại rồi, bạn import thủ công:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p hunr_2026 < sql/bo_mong_setup.sql
```

Hoặc trong container:
```bash
docker exec -i nro-db mysql -uroot -pYOUR_PASSWORD hunr_2026 < sql/bo_mong_setup.sql
```

---

## Backup database

Nếu dùng DB local/container, bạn có thể backup kiểu này:
```bash
mysqldump -h 127.0.0.1 -P 3306 -u root -p --databases hunr_2026 > backup/backup_$(date +%F_%H-%M-%S).sql
```

Nếu dùng container DB:
```bash
docker exec nro-db sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --databases "$MYSQL_DATABASE"' > backup/backup_$(date +%F_%H-%M-%S).sql
```

---

## Khuyến nghị tài nguyên cho máy nhỏ

Nếu VPS của bạn nhỏ, ví dụ 4 vCPU / 4 GB RAM:
- app Java: `XMS=512M`, `XMX=2G`
- MySQL: giữ mặc định trước, hoặc tune sau
- tránh mở quá nhiều thread pool nếu code đang set cao

Ví dụ `.env`:
```env
XMS=512M
XMX=2G
```

---

## Kiểm tra sự cố thường gặp

## 1) Java version sai
```bash
java -version
```
Phải là Java 17.

## 2) Port đã bị chiếm
```bash
ss -ltnp | grep 14445
ss -ltnp | grep 1707
```

## 3) App không lên vì thiếu data runtime
Triệu chứng thường là exception đọc file, map, image, data config.
Khi đó phải copy lại data từ máy Windows sang Linux.

## 4) App không lên vì DB password sai
Kiểm tra lại:
- `database.password`
- `spring.datasource.password`
- `.env` nếu chạy Docker

## 5) Client connect thất bại
Thường do 1 trong các lỗi:
- `game.servers` đang để IP localhost/127.0.0.1
- firewall chưa mở port `14445`
- cloud security group chưa mở port `14445`
- NAT/public IP chưa map đúng

## 6) Docker app start nhưng client không vào được
Kiểm tra:
```bash
docker compose logs -f app
ss -ltnp | grep 14445
sudo firewall-cmd --list-ports
```

---

## File đã được nâng cấp trong project này
- `pom.xml` → Java 17
- `Dockerfile` → builder/runtime Java 17
- `build.sh`, `run.sh`, `ngocrong.service` → Java 17 path
- `AutoBackup.java` → Linux dùng `backupsql.sh`
- `docker-compose.yml` → build/start app + mysql nhanh

---

## Quy trình gọn nhất mình khuyên dùng

Nếu bạn muốn lên Rocky 8.8 nhanh và ít lỗi nhất:
1. dùng **Docker Compose** để chạy `app + mysql`
2. copy đầy đủ runtime data từ máy Windows sang thư mục project
3. sửa `.env` với IP public thật
4. mở firewall `14445/tcp` và `1707/tcp`
5. test client connect từ máy ngoài

