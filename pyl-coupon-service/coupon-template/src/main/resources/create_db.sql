-- 登录 MySQL 服务器
mysql -hlocalhost -uroot -pDjangobbs

-- 创建数据库 pyl_coupon_data
CREATE DATABASE IF NOT EXISTS pyl_coupon_data;

-- 登录 MySQL 服务器, 并进入到 pyl_coupon_data 数据库中
mysql -hlocalhost -uroot -pDjangobbs -Dpyl_coupon_data
