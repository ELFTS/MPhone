#!/usr/bin/env python3
"""
生成 Minecraft 风格的应用图标 PNG 文件 (32x32 像素)
Minecraft 风格特点：像素化、简单几何、鲜明颜色、方块感
"""
from PIL import Image, ImageDraw
import os

# 应用定义：(文件名, 主色调, 绘制类型)
APPS = [
    # 系统应用
    ("contacts", "#3C44AA", "person"),      # 联系人 - 蓝色
    ("sms", "#5D8C22", "message"),          # 短信 - 绿色
    ("camera", "#B02E26", "camera"),        # 相机 - 红色
    ("settings", "#9D9D97", "gear"),        # 设置 - 灰色
    ("appstore", "#F9801D", "star"),        # 商店 - 橙色

    # 工具类
    ("compass", "#835432", "compass"),      # 指南针 - 棕色
    ("clock", "#3AB3DA", "clock"),          # 时钟 - 青色
    ("calculator", "#3C44AA", "calc"),      # 计算器 - 蓝色

    # 游戏类
    ("snake", "#5D8C22", "snake"),          # 贪吃蛇 - 绿色
    ("tetris", "#8932B8", "blocks"),        # 俄罗斯方块 - 紫色

    # 社交类
    ("chat", "#3C44AA", "chat"),            # 聊天室 - 蓝色
    ("mail", "#F9801D", "mail"),            # 邮件 - 橙色

    # 生产力类
    ("notes", "#FED83D", "note"),           # 备忘录 - 黄色
    ("calendar", "#C74EBD", "calendar"),    # 日历 - 粉色

    # 娱乐类
    ("music", "#169C9C", "music"),          # 音乐 - 青色
    ("gallery", "#B02E26", "image"),        # 相册 - 红色
]

def hex_to_rgb(hex_color):
    hex_color = hex_color.lstrip('#')
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def darken_color(rgb, factor=0.7):
    """加深颜色"""
    return tuple(int(c * factor) for c in rgb)

def lighten_color(rgb, factor=1.3):
    """减淡颜色"""
    return tuple(min(255, int(c * factor)) for c in rgb)

def draw_pixel_rect(draw, x, y, w, h, color, pixel_size=2):
    """绘制像素风格的矩形"""
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=color)

def draw_pixel_circle(draw, cx, cy, r, color, pixel_size=2):
    """绘制像素风格的圆（近似）"""
    for y in range(-r, r + 1):
        for x in range(-r, r + 1):
            if x * x + y * y <= r * r + r:
                draw.point([cx + x, cy + y], fill=color)

def rgb_to_hex(rgb):
    """将 RGB 元组转换为十六进制颜色字符串"""
    return '#{:02x}{:02x}{:02x}'.format(rgb[0], rgb[1], rgb[2])

def draw_minecraft_style_background(img, draw, bg_color):
    """绘制 Minecraft 风格的背景（带边框和渐变效果）"""
    size = 32
    # 转换颜色格式
    bg_hex = rgb_to_hex(bg_color)
    
    # 主背景
    draw.rectangle([0, 0, size - 1, size - 1], fill=bg_hex)
    
    # 顶部高光（Minecraft 风格）
    light_color = lighten_color(bg_color, 1.4)
    light_hex = rgb_to_hex(light_color)
    draw.rectangle([0, 0, size - 1, 3], fill=light_hex)
    draw.rectangle([0, 0, 3, size - 1], fill=light_hex)
    
    # 底部阴影
    dark_color = darken_color(bg_color, 0.6)
    dark_hex = rgb_to_hex(dark_color)
    draw.rectangle([0, size - 4, size - 1, size - 1], fill=dark_hex)
    draw.rectangle([size - 4, 0, size - 1, size - 1], fill=dark_hex)
    
    # 内边框
    inner_color = lighten_color(bg_color, 1.1)
    inner_hex = rgb_to_hex(inner_color)
    draw.rectangle([4, 4, size - 5, size - 5], outline=inner_hex, width=1)

def draw_person(draw, size, color):
    """绘制 Minecraft 风格的人形图标"""
    cx, cy = size // 2, size // 2
    # 简单的头部（方块）
    head_size = 8
    head_x, head_y = cx - head_size // 2, cy - 6
    draw.rectangle([head_x, head_y, head_x + head_size - 1, head_y + head_size - 1], fill="#FFFFFF")
    # 身体
    body_w, body_h = 12, 8
    body_x, body_y = cx - body_w // 2, head_y + head_size + 1
    draw.rectangle([body_x, body_y, body_x + body_w - 1, body_y + body_h - 1], fill="#FFFFFF")

def draw_message(draw, size, color):
    """绘制 Minecraft 风格的消息气泡"""
    # 气泡主体（圆角矩形）
    draw.rectangle([6, 6, 25, 22], fill="#FFFFFF")
    # 小尾巴
    draw.rectangle([6, 20, 12, 26], fill="#FFFFFF")
    # 内部线条表示文字
    draw.rectangle([10, 10, 21, 12], fill=color)
    draw.rectangle([10, 14, 18, 16], fill=color)

def draw_camera(draw, size, color):
    """绘制 Minecraft 风格的相机"""
    cx, cy = size // 2, size // 2
    # 相机机身
    body_w, body_h = 16, 12
    body_x, body_y = cx - body_w // 2, cy - body_h // 2
    draw.rectangle([body_x, body_y, body_x + body_w - 1, body_y + body_h - 1], fill="#FFFFFF")
    # 镜头（外圈）
    lens_r = 5
    draw_pixel_circle(draw, cx, cy, lens_r, color)
    # 镜头（内圈）
    draw_pixel_circle(draw, cx, cy, 2, "#FFFFFF")
    # 闪光灯
    flash_x = body_x + body_w - 4
    flash_y = body_y + 2
    draw.rectangle([flash_x, flash_y, flash_x + 2, flash_y + 2], fill="#FED83D")

def draw_gear(draw, size, color):
    """绘制 Minecraft 风格的齿轮"""
    cx, cy = size // 2, size // 2
    # 中心
    draw.rectangle([cx - 3, cy - 3, cx + 2, cy + 2], fill="#FFFFFF")
    # 四个方向的齿
    tooth_len = 4
    # 上
    draw.rectangle([cx - 2, cy - 6 - tooth_len, cx + 1, cy - 6], fill="#FFFFFF")
    # 下
    draw.rectangle([cx - 2, cy + 5, cx + 1, cy + 5 + tooth_len], fill="#FFFFFF")
    # 左
    draw.rectangle([cx - 6 - tooth_len, cy - 2, cx - 6, cy + 1], fill="#FFFFFF")
    # 右
    draw.rectangle([cx + 5, cy - 2, cx + 5 + tooth_len, cy + 1], fill="#FFFFFF")
    # 连接部分
    draw.rectangle([cx - 2, cy - 6, cx + 1, cy + 5], fill="#FFFFFF")
    draw.rectangle([cx - 6, cy - 2, cx + 5, cy + 1], fill="#FFFFFF")

def draw_star(draw, size, color):
    """绘制 Minecraft 风格的商店图标 - 店铺/建筑样式"""
    cx, cy = size // 2, size // 2
    
    # 商店主体（矩形建筑）
    shop_left = 6
    shop_right = 26
    shop_top = 12
    shop_bottom = 24
    
    # 建筑主体（白色墙体）
    draw.rectangle([shop_left, shop_top, shop_right, shop_bottom], fill="#FFFFFF")
    
    # 屋顶（三角形/梯形）- 使用主题色
    roof_color = "#F9801D"
    # 屋顶主体
    for row in range(6):
        y = 6 + row
        left_x = shop_left + row
        right_x = shop_right - row
        draw.rectangle([left_x, y, right_x, y + 1], fill=roof_color)
    
    # 屋顶边缘高光
    draw.rectangle([shop_left, 11, shop_right, 12], fill="#FFB74D")
    
    # 门（矩形）
    door_left = cx - 4
    door_right = cx + 4
    door_top = shop_bottom - 8
    draw.rectangle([door_left, door_top, door_right, shop_bottom], fill="#8D6E63")
    # 门把手
    draw.rectangle([door_right - 2, door_top + 3, door_right, door_top + 5], fill="#FFD54F")
    
    # 窗户（两侧）
    window_color = "#B3E5FC"
    # 左窗
    draw.rectangle([shop_left + 3, shop_top + 3, shop_left + 7, shop_top + 7], fill=window_color)
    draw.rectangle([shop_left + 4, shop_top + 5, shop_left + 6, shop_top + 5], fill="#FFFFFF")  # 窗框
    # 右窗
    draw.rectangle([shop_right - 7, shop_top + 3, shop_right - 3, shop_top + 7], fill=window_color)
    draw.rectangle([shop_right - 6, shop_top + 5, shop_right - 4, shop_top + 5], fill="#FFFFFF")  # 窗框
    
    # 商店招牌
    sign_y = shop_top - 2
    draw.rectangle([cx - 6, sign_y, cx + 6, sign_y + 3], fill="#FFFFFF", outline="#F9801D", width=1)
    # 招牌上的"A"字（表示App）
    draw.rectangle([cx - 1, sign_y + 1, cx + 1, sign_y + 2], fill="#F9801D")

def draw_compass(draw, size, color):
    """绘制 Minecraft 风格的指南针"""
    cx, cy = size // 2, size // 2
    r = 9
    # 外圈（方块近似）
    draw.rectangle([cx - r, cy - r, cx + r - 1, cy + r - 1], outline="#FFFFFF", width=2)
    # 北指针（红色）
    draw.rectangle([cx - 1, cy - r + 3, cx + 1, cy], fill="#B02E26")
    draw.rectangle([cx - 3, cy - 2, cx + 2, cy + 1], fill="#B02E26")
    # 南指针（白色）
    draw.rectangle([cx - 1, cy, cx + 1, cy + r - 3], fill="#FFFFFF")
    # 中心点
    draw.rectangle([cx - 1, cy - 1, cx + 1, cy + 1], fill="#FFFFFF")

def draw_clock(draw, size, color):
    """绘制 Minecraft 风格的时钟"""
    cx, cy = size // 2, size // 2
    r = 9
    # 外圈
    draw.rectangle([cx - r, cy - r, cx + r - 1, cy + r - 1], outline="#FFFFFF", width=2)
    # 刻度（简化）
    draw.rectangle([cx - 1, cy - r + 2, cx + 1, cy - r + 5], fill="#FFFFFF")  # 12点
    draw.rectangle([cx + r - 5, cy - 1, cx + r - 2, cy + 1], fill="#FFFFFF")  # 3点
    # 指针
    draw.rectangle([cx - 1, cy - r + 5, cx + 1, cy + 2], fill="#FFFFFF")  # 分针
    draw.rectangle([cx - 1, cy, cx + 5, cy + 2], fill="#FFFFFF")  # 时针

def draw_calc(draw, size, color):
    """绘制 Minecraft 风格的计算器"""
    # 机身
    draw.rectangle([6, 6, 25, 25], fill="#FFFFFF")
    # 显示屏
    draw.rectangle([9, 9, 22, 13], fill=color)
    # 按钮网格 (2x3)
    btn_colors = ["#9D9D97", "#9D9D97", "#9D9D97", "#9D9D97", "#9D9D97", "#9D9D97"]
    btn_positions = [
        (10, 16), (15, 16), (20, 16),
        (10, 21), (15, 21), (20, 21)
    ]
    for (bx, by), bc in zip(btn_positions, btn_colors):
        draw.rectangle([bx, by, bx + 3, by + 3], fill=bc)

def draw_snake(draw, size, color):
    """绘制 Minecraft 风格的贪吃蛇"""
    cx, cy = size // 2, size // 2
    # 蛇身（几个方块）
    body = [
        (cx - 6, cy), (cx, cy), (cx + 6, cy)
    ]
    for x, y in body:
        draw.rectangle([x - 3, y - 3, x + 2, y + 2], fill="#5D8C22")
    # 蛇头
    head_x, head_y = cx + 10, cy
    draw.rectangle([head_x - 3, head_y - 3, head_x + 2, head_y + 2], fill="#5D8C22")
    # 眼睛
    draw.rectangle([head_x + 1, head_y - 2, head_x + 2, head_y - 1], fill="#FFFFFF")
    # 苹果
    apple_x, apple_y = cx - 10, cy - 6
    draw.rectangle([apple_x - 3, apple_y - 3, apple_x + 2, apple_y + 2], fill="#B02E26")
    draw.rectangle([apple_x, apple_y - 5, apple_x + 1, apple_y - 3], fill="#5D8C22")  # 叶子

def draw_blocks(draw, size, color):
    """绘制 Minecraft 风格的俄罗斯方块"""
    cx, cy = size // 2, size // 2
    block = 5
    # L形方块
    blocks = [
        (cx - block, cy - block),
        (cx, cy - block),
        (cx + block, cy - block),
        (cx - block, cy),
    ]
    for x, y in blocks:
        # 方块主体
        draw.rectangle([x - 3, y - 3, x + 3, y + 3], fill="#8932B8")
        # 高光
        draw.rectangle([x - 3, y - 3, x + 3, y - 2], fill="#B86CD6")
        # 阴影
        draw.rectangle([x - 3, y + 2, x + 3, y + 3], fill="#5A1E7A")

def draw_chat(draw, size, color):
    """绘制 Minecraft 风格的聊天气泡"""
    # 气泡
    draw.rectangle([6, 6, 25, 22], fill="#FFFFFF")
    # 尾巴
    draw.rectangle([6, 20, 10, 26], fill="#FFFFFF")
    # 文字线条
    draw.rectangle([10, 10, 21, 12], fill=color)
    draw.rectangle([10, 14, 18, 16], fill=color)
    draw.rectangle([10, 18, 16, 20], fill=color)

def draw_mail(draw, size, color):
    """绘制 Minecraft 风格的信封"""
    cx, cy = size // 2, size // 2
    
    # 信封主体（白色）
    draw.rectangle([7, 9, 24, 22], fill="#FFFFFF")
    
    # 信封翻盖（V形）- 使用主题色
    v_points_top = [
        (7, 9), (cx, 16), (24, 9)
    ]
    # 手动填充V形区域
    for row in range(7):
        y = 9 + row
        left_x = 7 + row
        right_x = 24 - row
        if left_x <= right_x:
            draw.rectangle([left_x, y, right_x, y + 1], fill="#F9801D")
    
    # 信封底部（稍深的白色）
    draw.rectangle([7, 17, 24, 22], fill="#F0F0F0")
    
    # 信封边框线
    draw.rectangle([7, 9, 24, 22], outline="#E0E0E0", width=1)

def draw_note(draw, size, color):
    """绘制 Minecraft 风格的便签"""
    # 便签纸
    draw.rectangle([6, 6, 25, 25], fill="#FED83D")
    # 顶部深色条
    draw.rectangle([6, 6, 25, 10], fill="#C7A800")
    # 线条
    draw.rectangle([9, 14, 22, 16], fill="#9D9D97")
    draw.rectangle([9, 18, 20, 20], fill="#9D9D97")
    draw.rectangle([9, 22, 18, 24], fill="#9D9D97")

def draw_calendar(draw, size, color):
    """绘制 Minecraft 风格的日历"""
    # 日历主体
    draw.rectangle([6, 8, 25, 25], fill="#FFFFFF")
    # 顶部条
    draw.rectangle([6, 8, 25, 13], fill="#C74EBD")
    # 挂环
    draw.rectangle([9, 5, 11, 10], fill="#9D9D97")
    draw.rectangle([20, 5, 22, 10], fill="#9D9D97")
    # 日期数字 "1"
    draw.rectangle([14, 16, 17, 22], fill="#C74EBD")

def draw_music(draw, size, color):
    """绘制 Minecraft 风格的音乐符号"""
    cx, cy = size // 2, size // 2
    # 音符头
    draw.rectangle([cx - 6, cy + 2, cx + 2, cy + 8], fill="#FFFFFF")
    # 音符杆
    draw.rectangle([cx + 2, cy - 8, cx + 4, cy + 5], fill="#FFFFFF")
    # 音符旗
    draw.rectangle([cx + 4, cy - 8, cx + 10, cy - 6], fill="#FFFFFF")
    draw.rectangle([cx + 4, cy - 6, cx + 8, cy - 4], fill="#FFFFFF")

def draw_image(draw, size, color):
    """绘制 Minecraft 风格的图片/相册"""
    # 相框
    draw.rectangle([6, 6, 25, 25], outline="#FFFFFF", width=2)
    # 山（三角形）
    draw.rectangle([8, 18, 23, 23], fill="#FFFFFF")
    draw.rectangle([12, 14, 19, 18], fill="#FFFFFF")
    draw.rectangle([15, 10, 17, 14], fill="#FFFFFF")
    # 太阳
    draw.rectangle([18, 10, 22, 14], fill="#FED83D")

DRAW_FUNCTIONS = {
    "person": draw_person,
    "message": draw_message,
    "camera": draw_camera,
    "gear": draw_gear,
    "star": draw_star,
    "compass": draw_compass,
    "clock": draw_clock,
    "calc": draw_calc,
    "snake": draw_snake,
    "blocks": draw_blocks,
    "chat": draw_chat,
    "mail": draw_mail,
    "note": draw_note,
    "calendar": draw_calendar,
    "music": draw_music,
    "image": draw_image,
}

def generate_icon(filename, bg_color, draw_type, size=32):
    """生成单个 Minecraft 风格图标"""
    bg_rgb = hex_to_rgb(bg_color)
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 绘制 Minecraft 风格背景
    draw_minecraft_style_background(img, draw, bg_rgb)
    
    # 绘制图标内容
    if draw_type in DRAW_FUNCTIONS:
        DRAW_FUNCTIONS[draw_type](draw, size, bg_rgb)
    
    return img

def main():
    output_dir = "src/main/resources/assets/mphone/textures/apps"
    os.makedirs(output_dir, exist_ok=True)

    for filename, bg_color, draw_type in APPS:
        img = generate_icon(filename, bg_color, draw_type)
        filepath = os.path.join(output_dir, f"{filename}.png")
        img.save(filepath)
        print(f"Generated: {filepath}")

    print(f"\nAll {len(APPS)} Minecraft-style app icons (32x32) generated successfully!")

if __name__ == "__main__":
    main()
