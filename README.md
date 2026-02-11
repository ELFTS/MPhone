# MPhone Mod - 我的世界手机模组
# Minecraft Phone Mod

这是一个为Minecraft 1.12.2开发的手机模组。

A phone mod developed for Minecraft 1.12.2.

## 功能特点
## Features

- 添加手机物品到游戏中
- Add phone item to the game
- 右键点击手机可以打开GUI界面
- Right-click phone to open GUI
- **全面屏绿色手机**设计
- **Full-screen green phone** design
- 手机界面包含6个应用按钮: 联系人、短信、相机、设置、应用、商店
- Phone interface includes 6 app buttons: Contacts, Messages, Camera, Settings, Apps, Store
- **真实游戏时间显示** - 状态栏显示Minecraft世界时间
- **Real game time display** - Status bar shows Minecraft world time
- **信号强度显示** - 状态栏显示基站信号强度和SIM卡状态
- **Signal strength display** - Status bar shows base station signal and SIM card status
- **相机拍照功能** - 点击相机按钮拍摄照片并保存
- **Camera photo function** - Click camera button to take and save photos
- **短信系统** - 发送和接收短信，支持多行消息气泡
- **SMS system** - Send and receive messages with multi-line bubbles
- **联系人功能** - 每个手机独立的联系人列表，支持添加、编辑、删除联系人，彩色头像显示，详情页可直接发短信
- **Contacts feature** - Independent contact list per phone, add/edit/delete contacts, colorful avatars, send SMS from details page
- **应用商店** - 免费安装/卸载各种应用
- **App Store** - Free install/uninstall various apps
- **手机设置** - 声音、通知、壁纸等设置
- **Phone Settings** - Sound, notification, wallpaper settings
- **电话卡系统** - 6位电话号码，插入SIM卡才能使用通信功能
- **SIM card system** - 6-digit phone number, SIM required for communication
- **基站系统** - 放置基站提供信号覆盖，无信号时无法发送短信
- **Base station system** - Place base stations for signal coverage, no SMS without signal
- **应用状态记忆** - 关闭GUI后重新打开会回到上次使用的应用
- **App state memory** - Reopen GUI returns to last used app
- **数据持久化** - 短信数据保存到世界存档，退出游戏不丢失
- **Data persistence** - SMS data saved to world, persists after game exit
- 支持中英文本地化
- Support Chinese and English localization
- 创造模式工具栏可找到手机物品
- Phone items available in creative inventory

## 环境要求
## Requirements

- Java 8 (JDK 1.8)
- Minecraft 1.12.2
- Forge 1.12.2-14.23.5.2859+

## 开发环境设置
## Development Environment Setup

1. 确保已安装Java 8
   Ensure Java 8 is installed
2. 在项目目录下运行:
   Run in project directory:
   ```bash
   # 生成IDE运行配置
   # Generate IDE run configurations
   gradlew genIntellijRuns  # 如果使用IntelliJ IDEA
                            # If using IntelliJ IDEA
   # 或
   # or
   gradlew genEclipseRuns   # 如果使用Eclipse
                            # If using Eclipse
   ```

## 构建模组
## Build Mod

```bash
# 构建模组文件
# Build mod file
gradlew build
```

构建完成后，模组文件将位于 `build/libs/MPhone-1.12.2-1.0.1.jar`

After build, mod file will be at `build/libs/MPhone-1.12.2-1.0.1.jar`

## 安装模组
## Installation

1. 确保已安装Forge 1.12.2
   Ensure Forge 1.12.2 is installed
2. 将 `MPhone-1.12.2-1.0.1.jar` 复制到 `.minecraft/mods/` 文件夹
   Copy `MPhone-1.12.2-1.0.1.jar` to `.minecraft/mods/` folder
3. 启动Minecraft，选择Forge版本
   Launch Minecraft, select Forge version
4. 在创造模式工具栏中找到"MPhone手机"、"电话卡"和"信号基站"
   Find "MPhone", "SIM Card" and "Base Station" in creative inventory

## 运行测试
## Run Tests

```bash
# 运行客户端测试
# Run client test
gradlew runClient

# 运行服务端测试
# Run server test
gradlew runServer
```

## 常用Gradle命令
## Common Gradle Commands

| 命令 | 说明 |
| Command | Description |
|------|------|
| `gradlew build` | 构建模组 |
| | Build mod |
| `gradlew runClient` | 运行客户端 |
| | Run client |
| `gradlew runServer` | 运行服务端 |
| | Run server |
| `gradlew clean` | 清理构建文件 |
| | Clean build files |
| `gradlew tasks` | 查看所有可用任务 |
| | View all available tasks |

## 使用说明
## Usage Guide

### 信号基站
### Signal Base Station

**功能介绍:**
**Features:**
- 提供64格半径的信号覆盖范围
- Provides 64-block radius signal coverage
- 手机需要在基站范围内才能显示信号
- Phone needs to be within range to show signal
- 信号强度根据距离衰减：4格(0-16格) → 3格(16-32格) → 2格(32-48格) → 1格(48-64格)
- Signal strength decays with distance: 4 bars(0-16) → 3 bars(16-32) → 2 bars(32-48) → 1 bar(48-64)

**使用方法:**
**How to use:**
1. 在创造模式工具栏中找到"信号基站"
   Find "Signal Base Station" in creative inventory
2. 放置在世界中任意位置
   Place anywhere in the world
3. 基站会发光，作为装饰和功能性方块
   Base station glows, serves as decorative and functional block

### 联系人
### Contacts

**功能介绍:**
**Features:**
- 每个手机拥有独立的联系人列表（基于SIM卡号码隔离）
- Each phone has independent contact list (isolated by SIM number)
- 添加、查看、编辑和删除联系人
- Add, view, edit and delete contacts
- 联系人显示彩色头像（名字首字）
- Contacts show colorful avatars (first character of name)
- 联系人详情页可直接发送短信
- Send SMS directly from contact details
- 联系人数据保存到世界存档
- Contact data saved to world
- 在短信系统中自动显示联系人名称
- Auto display contact names in SMS system

**使用方法:**
**How to use:**
1. 打开手机GUI，点击"联系人"按钮
   Open phone GUI, click "Contacts" button
2. **联系人列表界面:**
   **Contact List Interface:**
   - 查看当前手机已保存的联系人
   - View saved contacts for current phone
   - 点击"新建"按钮添加联系人
   - Click "New" to add contact
   - 点击"查看"按钮查看联系人详情
   - Click "View" to see contact details
3. **联系人详情界面:**
   **Contact Details Interface:**
   - 显示联系人头像、姓名和电话
   - Shows avatar, name and phone
   - 点击"发短信"直接给该联系人发送短信
   - Click "Send SMS" to message contact
   - 点击"编辑"修改联系人信息
   - Click "Edit" to modify contact
   - 点击"删除"删除联系人
   - Click "Delete" to remove contact
4. **新建/编辑联系人:**
   **New/Edit Contact:**
   - 输入联系人姓名（最多20字）
   - Enter contact name (max 20 chars)
   - 输入6位电话号码
   - Enter 6-digit phone number
   - 点击"保存"
   - Click "Save"

**注意:** 每个SIM卡（每个手机）的联系人列表是独立的，更换SIM卡后会显示该SIM卡对应的联系人列表
**Note:** Each SIM card (each phone) has independent contact list, changing SIM shows that SIM's contacts

### 短信系统
### SMS System

**功能介绍:**
**Features:**
- 发送和接收短信消息
- Send and receive SMS messages
- 支持多行消息气泡显示（自动换行）
- Multi-line message bubbles (auto wrap)
- 会话列表显示所有联系人
- Conversation list shows all contacts
- 未读消息标记（红色圆点）
- Unread message indicator (red dot)
- 需要信号基站才能发送短信
- Base station required to send SMS
- 短信数据保存到世界存档，退出游戏不丢失
- SMS data saved to world, persists after exit

**使用方法:**
**How to use:**
1. 确保已插入SIM卡且在信号基站范围内
   Ensure SIM inserted and within base station range
2. 打开手机GUI，点击"短信"按钮
   Open phone GUI, click "Messages" button
3. **会话列表界面:**
   **Conversation List Interface:**
   - 查看所有短信会话
   - View all SMS conversations
   - 点击"新建"按钮发送新短信
   - Click "New" to send new message
   - 点击会话查看聊天记录
   - Click conversation to view chat history
4. **聊天界面:**
   **Chat Interface:**
   - 绿色气泡 = 你发送的消息
   - Green bubble = Your message
   - 灰色气泡 = 收到的消息
   - Gray bubble = Received message
   - 输入消息后点击"发送"或按回车
   - Type message and click "Send" or press Enter
5. **新建短信:**
   **New Message:**
   - 输入6位收件人号码
   - Enter 6-digit recipient number
   - 输入短信内容（最多100字）
   - Enter message content (max 100 chars)
   - 点击"发送"
   - Click "Send"

### 电话卡
### SIM Card

**功能介绍:**
**Features:**
- 每张电话卡有唯一的6位电话号码
- Each SIM has unique 6-digit phone number
- 必须插入手机才能使用通信功能
- Must insert into phone for communication
- 可在物品栏中查看电话号码
- Can view phone number in inventory

**使用方法:**
**How to use:**
1. 在创造模式工具栏中找到"电话卡"
   Find "SIM Card" in creative inventory
2. 手持电话卡，右键点击手机即可插入
   Hold SIM card, right-click phone to insert
3. 潜行+右键手机可取出电话卡
   Sneak+right-click to remove SIM

### 如何使用手机
### How to Use Phone

1. 在创造模式工具栏中找到"MPhone手机"
   Find "MPhone" in creative inventory
2. 将手机拿在手中，右键点击打开GUI界面
   Hold phone, right-click to open GUI
3. **状态栏显示:**
   **Status Bar Display:**
   - 左侧：信号强度（4格显示，需要插入SIM卡且在基站范围内）
   - Left: Signal strength (4 bars, requires SIM and base station)
   - 中间：游戏时间
   - Center: Game time
   - 右侧：SIM卡状态（绿色=已插卡，灰色=未插卡）
   - Right: SIM status (green=inserted, gray=not inserted)
4. 在GUI界面中可以看到应用图标（5行3列布局，每页最多15个）
   GUI shows app icons (5 rows x 3 columns, 15 per page):
   - **系统应用**（固定显示）
   - **System Apps** (Fixed display)
     - **联系人** - 查看和管理联系人
     - **Contacts** - View and manage contacts
     - **短信** - 发送和接收短信
     - **Messages** - Send and receive SMS
     - **相机** - 拍照功能
     - **Camera** - Photo function
     - **设置** - 手机设置（声音、通知、壁纸）
     - **Settings** - Phone settings (sound, notification, wallpaper)
     - **商店** - 应用商店
     - **Store** - App store
   - **已安装应用** - 从应用商店安装的应用会显示在这里
   - **Installed Apps** - Apps from store show here
5. **翻页浏览** - 如果应用超过15个，点击左右箭头翻页
   **Page Navigation** - If more than 15 apps, click arrows to navigate
6. **应用状态记忆** - 关闭GUI后重新打开会自动回到上次使用的应用界面
   **App State Memory** - Reopen GUI returns to last used app

### 手机设置
### Phone Settings

**功能介绍:**
**Features:**
- **声音** - 开启/关闭手机声音
- **Sound** - Enable/disable phone sound
- **通知** - 开启/关闭通知
- **Notification** - Enable/disable notifications
- **壁纸** - 更换手机壁纸（默认/蓝色/绿色/紫色/暗色）
- **Wallpaper** - Change wallpaper (default/blue/green/purple/dark)
- **关于手机** - 显示版本信息
- **About Phone** - Show version info

**使用方法:**
**How to use:**
1. 打开手机GUI，点击"设置"按钮
   Open phone GUI, click "Settings" button
2. 点击对应选项进行设置
   Click options to configure
3. 点击左上角"←"返回主界面
   Click "←" top-left to return

### 应用商店
### App Store

**功能介绍:**
**Features:**
- 浏览和安装各种应用
- Browse and install various apps
- 按分类筛选: 全部、工具、游戏、社交、生产力、娱乐
- Filter by category: All, Tools, Games, Social, Productivity, Entertainment
- 免费安装和卸载应用
- Free install and uninstall apps
- 支持上下滚动浏览所有应用
- Scroll to browse all apps

**内置应用列表:**
**Built-in Apps:**

| 应用名称 | 分类 | 说明 |
| App Name | Category | Description |
|---------|------|------|
| 指南针 | 工具 | 显示方向的实用工具 |
| Compass | Tools | Shows direction |
| 时钟 | 工具 | 显示当前游戏时间 |
| Clock | Tools | Shows current game time |
| 计算器 | 工具 | 进行基本数学计算 |
| Calculator | Tools | Basic math calculations |
| 贪吃蛇 | 游戏 | 经典贪吃蛇游戏 |
| Snake | Games | Classic snake game |
| 俄罗斯方块 | 游戏 | 经典方块消除游戏 |
| Tetris | Games | Classic block puzzle |
| 聊天室 | 社交 | 与服务器玩家聊天 |
| Chat Room | Social | Chat with server players |
| 邮件 | 社交 | 发送和接收邮件 |
| Mail | Social | Send and receive mail |
| 备忘录 | 生产力 | 记录重要事项 |
| Memo | Productivity | Record important notes |
| 日历 | 生产力 | 查看游戏内日期 |
| Calendar | Productivity | View in-game date |
| 音乐播放器 | 娱乐 | 播放游戏音乐 |
| Music Player | Entertainment | Play game music |
| 相册 | 娱乐 | 查看拍摄的照片 |
| Gallery | Entertainment | View taken photos |

**使用方法:**
**How to use:**
1. 打开手机GUI，点击"商店"按钮
   Open phone GUI, click "Store" button
2. 在应用商店界面浏览应用列表
   Browse app list in store interface
3. 点击顶部分类按钮筛选应用类型
   Click category buttons to filter
4. 点击应用右侧的"安装"按钮安装应用
   Click "Install" to install app
5. 已安装的应用会显示"卸载"按钮，点击可卸载
   Installed apps show "Uninstall" button

### 相机功能
### Camera Function

**使用方法:**
**How to use:**
1. 打开手机GUI
   Open phone GUI
2. 点击"相机"按钮
   Click "Camera" button
3. 等待3秒倒计时 (3... 2... 1...)
   Wait 3-second countdown (3... 2... 1...)
4. 照片自动保存到 `screenshots/mphone/` 目录
   Photo auto-saved to `screenshots/mphone/`
5. 照片带有水印: "MPhone Camera" + 拍摄时间
   Photo has watermark: "MPhone Camera" + timestamp

**照片特点:**
**Photo Features:**
- 格式: PNG
- Format: PNG
- 分辨率: 游戏窗口分辨率
- Resolution: Game window resolution
- 保存路径: `.minecraft/screenshots/mphone/`
- Save path: `.minecraft/screenshots/mphone/`
- 文件名: `MPhone_Photo_yyyy-MM-dd_HH-mm-ss.png`
- Filename: `MPhone_Photo_yyyy-MM-dd_HH-mm-ss.png`
- 自动添加水印
- Auto watermark

### GUI界面特点
### GUI Features

- **全面屏设计** - 绿色外壳
- **Full-screen design** - Green shell
- **竖屏比例** - 120x220像素
- **Portrait ratio** - 120x220 pixels
- **15个应用按钮** - 5行3列布局，方形设计，彩色图标，支持翻页
- **15 app buttons** - 5x3 grid, square design, colorful icons, page support
- **状态栏** - 顶部显示，包含信号强度、时间、SIM卡状态
- **Status bar** - Top display with signal, time, SIM status
- **视觉效果** - 按钮悬停高亮、立体阴影、文字阴影效果
- **Visual effects** - Button hover highlight, 3D shadows, text shadows

### 时间显示
### Time Display

手机状态栏显示Minecraft世界时间:
Phone status bar shows Minecraft world time:
- 格式: 12小时制 (例如: 3:30 PM)
- Format: 12-hour (e.g., 3:30 PM)
- 基于游戏内世界时间
- Based on in-game world time
- 0 tick = 6:00 AM
- 6000 tick = 12:00 PM
- 12000 tick = 6:00 PM
- 18000 tick = 12:00 AM

## 更新日志
## Changelog

### v1.0.1 (2025-02-11)

**新增 Features:**
- 应用图标支持PNG格式显示
- App icons now support PNG format display

**修复 Fixes:**
- 修复应用图标文字遮挡问题
- Fixed app icon text overlap

**如果需要查看之前的版本请访问：https://modrinth.com/mod/mphone/changelog**

## 许可证
## License

GNU General Public License v3.0 (GPL-3.0)

---
