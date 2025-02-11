/*
 Navicat Premium Data Transfer

 Source Server         : dict_v1
 Source Server Type    : SQLite
 Source Server Version : 3035005
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3035005
 File Encoding         : 65001

 Date: 10/02/2025 10:46:24
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for C01_初中词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C01_初中词汇正序版";
CREATE TABLE "C01_初中词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

-- ----------------------------
-- Table structure for C02_高中英语词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C02_高中英语词汇正序版";
CREATE TABLE "C02_高中英语词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

-- ----------------------------
-- Table structure for C03_四级词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C03_四级词汇正序版";
CREATE TABLE "C03_四级词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

-- ----------------------------
-- Table structure for C04_六级词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C04_六级词汇正序版";
CREATE TABLE "C04_六级词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

-- ----------------------------
-- Table structure for C05_2013考研词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C05_2013考研词汇正序版";
CREATE TABLE "C05_2013考研词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

-- ----------------------------
-- Table structure for C06_雅思词汇正序版
-- ----------------------------
DROP TABLE IF EXISTS "C06_雅思词汇正序版";
CREATE TABLE "C06_雅思词汇正序版" (
  "单词" TEXT,
  "英音" TEXT,
  "美音" TEXT,
  "释义" TEXT,
  "等级" TEXT
);

PRAGMA foreign_keys = true;
