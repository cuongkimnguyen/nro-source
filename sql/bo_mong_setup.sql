-- Database Schema cho NPC Bò Mộng
-- Tạo các bảng cấu hình và lưu trữ

-- Bảng cấu hình nhiệm vụ
CREATE TABLE IF NOT EXISTS nr_bo_mong_nhiem_vu_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    loai_nv TINYINT NOT NULL COMMENT '1: Kill Mob, 2: Nạp tiền, 3: Đạt SM, 4: Kill Boss, 5: Nâng trang bị, 6: Train, 7: Làm Task',
    do_kho TINYINT NOT NULL COMMENT '0: Easy, 1: Normal, 2: Hard',
    yeu_cau_min INT NOT NULL COMMENT 'Yêu cầu tối thiểu',
    yeu_cau_max INT NOT NULL COMMENT 'Yêu cầu tối đa',
    diem_min INT NOT NULL COMMENT 'Điểm thưởng tối thiểu',
    diem_max INT NOT NULL COMMENT 'Điểm thưởng tối đa',
    active TINYINT DEFAULT 1 COMMENT '1: Active, 0: Inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_loai_do_kho (loai_nv, do_kho),
    INDEX idx_loai_nv (loai_nv),
    INDEX idx_do_kho (do_kho)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cấu hình nhiệm vụ Bò Mộng';

-- Insert dữ liệu mặc định
INSERT INTO nr_bo_mong_nhiem_vu_config (loai_nv, do_kho, yeu_cau_min, yeu_cau_max, diem_min, diem_max) VALUES
-- Kill Mob
(1, 0, 100, 500, 1, 5),
(1, 1, 501, 1999, 5, 15),
(1, 2, 2000, 5000, 15, 50),

-- Nạp Tiền
(2, 0, 10000, 50000, 1, 5),
(2, 1, 50000, 200000, 5, 15),
(2, 2, 200000, 1000000, 15, 50),

-- Đạt SM
(3, 0, 1000000000, 10000000000, 1, 5),
(3, 1, 10000000000, 50000000000, 5, 15),
(3, 2, 50000000000, 999999999999, 15, 50),

-- Kill Boss
(4, 0, 3, 5, 1, 5),
(4, 1, 2, 3, 5, 15),
(4, 2, 1, 2, 15, 50),

-- Nâng Trang Bị
(5, 0, 1, 1, 1, 5),
(5, 1, 3, 3, 5, 15),
(5, 2, 5, 5, 15, 50),

-- Train
(6, 0, 3600, 3600, 1, 5),
(6, 1, 7200, 7200, 5, 15),
(6, 2, 10800, 10800, 15, 50),

-- Làm Task
(7, 0, 1, 3, 1, 5),
(7, 1, 3, 5, 5, 15),
(7, 2, 5, 10, 15, 50);

-- Bảng cấu hình boss
CREATE TABLE IF NOT EXISTS nr_bo_mong_boss_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    do_kho TINYINT NOT NULL COMMENT '0: Easy, 1: Normal, 2: Hard',
    boss_class_name VARCHAR(100) NOT NULL COMMENT 'Tên class của boss',
    active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_do_kho (do_kho)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cấu hình boss cho nhiệm vụ Kill Boss';

-- Insert dữ liệu boss
INSERT INTO nr_bo_mong_boss_config (do_kho, boss_class_name) VALUES
-- Easy Boss
(0, 'XenCon'),
(0, 'BlackGoku'),
(0, 'KuKu'),
(0, 'RamBo'),
(0, 'MapDauDinh'),

-- Normal Boss
(1, 'TeamAndroid13'),
(1, 'TeamAndroid19'),
(1, 'TeamAndroid16'),
(1, 'Chilled'),
(1, 'Bardock_ThoiKhong'),

-- Hard Boss
(2, 'XenBoHung'),
(2, 'SieuBoHung'),
(2, 'SuperBroly'),
(2, 'ZamasuFusion');

-- Bảng cấu hình mốc điểm và quà (tạm chưa dùng)
CREATE TABLE IF NOT EXISTS nr_bo_mong_moc_diem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    diem_can_thiet INT NOT NULL COMMENT 'Số điểm cần thiết để nổ hũ',
    item_id_1 INT NOT NULL,
    item_id_2 INT NOT NULL,
    item_id_3 INT NOT NULL,
    item_id_4 INT NOT NULL,
    quantity_1 INT DEFAULT 1,
    quantity_2 INT DEFAULT 1,
    quantity_3 INT DEFAULT 1,
    quantity_4 INT DEFAULT 1,
    active TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_diem (diem_can_thiet),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cấu hình mốc điểm và quà nổ hũ';

-- Bảng cấu hình tỷ lệ và tham số
CREATE TABLE IF NOT EXISTS nr_bo_mong_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(50) NOT NULL,
    config_value VARCHAR(255) NOT NULL,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cấu hình tỷ lệ và tham số hệ thống';

-- Insert cấu hình mặc định
INSERT INTO nr_bo_mong_config (config_key, config_value, description) VALUES
('ty_le_easy', '50', 'Tỷ lệ random nhiệm vụ Easy (%)'),
('ty_le_normal', '35', 'Tỷ lệ random nhiệm vụ Normal (%)'),
('ty_le_hard', '15', 'Tỷ lệ random nhiệm vụ Hard (%)'),
('max_nv_per_day', '10', 'Số nhiệm vụ tối đa mỗi ngày'),
('min_diem', '1', 'Điểm tối thiểu'),
('max_diem', '1000', 'Điểm tối đa'),
('max_task_id', '26', 'Task ID tối đa'),
('max_retry_random', '10', 'Số lần retry khi random nhiệm vụ'),
('enable_huy_nv', '1', 'Cho phép hủy nhiệm vụ'),
('max_huy_per_day', '999', 'Số lần hủy nhiệm vụ tối đa mỗi ngày');

-- Bảng lưu nhiệm vụ hiện tại của player
CREATE TABLE IF NOT EXISTS nr_bo_mong_nhiem_vu (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    loai_nv TINYINT NOT NULL,
    do_kho TINYINT NOT NULL,
    yeu_cau INT NOT NULL,
    tien_do INT DEFAULT 0,
    diem_thuong INT NOT NULL DEFAULT 0,
    trang_thai TINYINT DEFAULT 0 COMMENT '0: Đang làm, 1: Hoàn thành',
    created_at BIGINT NOT NULL,
    INDEX idx_player_id (player_id),
    INDEX idx_trang_thai (trang_thai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Nhiệm vụ hiện tại của player';

-- Bảng lịch sử nổ hũ (tạm chưa dùng)
CREATE TABLE IF NOT EXISTS nr_bo_mong_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    diem_su_dung INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    created_at BIGINT NOT NULL,
    INDEX idx_player_id (player_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Lịch sử nổ hũ của player';

-- Thêm cột vào bảng nr_player
ALTER TABLE nr_player 
ADD COLUMN IF NOT EXISTS point_bo_mong INT DEFAULT 0 COMMENT 'Điểm tích lũy Bò Mộng',
ADD COLUMN IF NOT EXISTS count_nhiem_vu_bo_mong INT DEFAULT 0 COMMENT 'Số nhiệm vụ đã làm hôm nay',
ADD COLUMN IF NOT EXISTS last_reset_nv_bo_mong BIGINT DEFAULT 0 COMMENT 'Thời gian reset NV',
ADD COLUMN IF NOT EXISTS last_coin_value INT DEFAULT 0 COMMENT 'Coin cũ để check nạp tiền',
ADD COLUMN IF NOT EXISTS count_task_completed_today INT DEFAULT 0 COMMENT 'Số task đã hoàn thành hôm nay';

ALTER TABLE nr_player ADD INDEX IF NOT EXISTS idx_point_bo_mong (point_bo_mong);

