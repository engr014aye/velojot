import os
import math
from PIL import Image, ImageDraw, ImageFont

NAVY_DARK = (0, 24, 69)         # #001845
NAVY_ACCENT = (2, 33, 92)       # #02215c
NAVY_SURFACE = (10, 25, 47)      # #0a192f
NAVY_CARD = (15, 33, 64)         # #0f2140
ORANGE_VIBRANT = (255, 153, 0)   # #FF9900
ORANGE_SOFT = (255, 179, 64)     # #FFB340
WHITE = (255, 255, 255)          # #FFFFFF
SLATE_LIGHT = (248, 250, 252)    # #F8FAFC
SLATE_MUTED = (148, 163, 184)    # #94A3B8
GREEN_CHECK = (16, 185, 129)     # #10B981
CARD_ACCENT_1 = (254, 243, 199)  # Amber soft
CARD_ACCENT_2 = (224, 231, 255)  # Indigo soft
CARD_ACCENT_3 = (209, 250, 229)  # Mint soft

def draw_flat_logo(draw, cx, cy, size):
    """
    Draws the flat 2D geometric VeloJot icon.
    Icon consists of:
    - Stylized geometric "V" in Vibrant Orange (#FF9900)
    - Pen nib silhouette in Clean White (#FFFFFF)
    - Slit and breather hole in Deep Navy (#001845)
    - Speed/jot dynamic accent in Clean White (#FFFFFF)
    """
    scale = size / 100.0
    ox = cx - 54 * scale
    oy = cy - 54 * scale

    def pt(x, y):
        return (ox + x * scale, oy + y * scale)

    # Stylized V left wing
    left_v = [pt(27, 34), pt(41, 34), pt(54, 64), pt(44, 68), pt(27, 34)]
    draw.polygon(left_v, fill=ORANGE_VIBRANT)

    # Stylized V right wing
    right_v = [pt(67, 34), pt(81, 34), pt(57, 80), pt(47, 76), pt(67, 34)]
    draw.polygon(right_v, fill=ORANGE_VIBRANT)

    # Center pen nib silhouette in clean white
    nib = [pt(48, 28), pt(60, 28), pt(64, 40), pt(54, 58), pt(44, 40)]
    draw.polygon(nib, fill=WHITE)

    # Breather hole
    br_cx, br_cy = pt(54, 37)
    br_r = 2 * scale
    draw.ellipse([br_cx - br_r, br_cy - br_r, br_cx + br_r, br_cy + br_r], fill=NAVY_DARK)

    # Nib slit
    slit_w = max(1.0, 1.2 * scale)
    slit_top = pt(54, 38)[1]
    slit_bottom = pt(54, 54)[1]
    slit_x = pt(54, 38)[0]
    draw.rectangle([slit_x - slit_w / 2, slit_top, slit_x + slit_w / 2, slit_bottom], fill=NAVY_DARK)

    # Speed jot accent
    jot = [pt(70, 24), pt(76, 24), pt(72, 32), pt(66, 32)]
    draw.polygon(jot, fill=WHITE)


def generate_app_icon_512(output_path):
    # Google Play Store Icon: Exactly 512x512, 32-bit PNG, no rounded corners (full square bleed), no transparent background
    img = Image.new("RGBA", (512, 512), NAVY_DARK + (255,))
    draw = ImageDraw.Draw(img)

    # Subtle flat geometric diagonal tone split
    draw.polygon([(0, 0), (512, 512), (512, 0)], fill=NAVY_ACCENT + (255,))

    # Draw flat logo centered
    draw_flat_logo(draw, cx=256, cy=256, size=320)

    img.save(output_path, "PNG")
    print(f"Generated 512x512 Icon: {output_path} ({img.size})")


def generate_feature_graphic_1024x500(output_path):
    # Google Play Feature Graphic: Exactly 1024x500 px PNG
    img = Image.new("RGBA", (1024, 500), NAVY_DARK + (255,))
    draw = ImageDraw.Draw(img)

    # Flat geometric angled background panels
    draw.polygon([(480, 0), (1024, 0), (1024, 500), (320, 500)], fill=(2, 28, 77, 255))
    draw.polygon([(700, 0), (1024, 0), (1024, 500), (620, 500)], fill=(4, 38, 102, 255))

    # Top accent line
    draw.rectangle([(0, 0), (1024, 6)], fill=ORANGE_VIBRANT + (255,))

    # Fonts
    font_title = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 46)
    font_tagline = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 22)
    font_body = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 17)
    font_badge = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 13)

    # Left Section: Icon & Typography
    icon_box_size = 100
    icon_x = 70
    icon_y = 65

    # Icon backing badge
    draw.rounded_rectangle(
        [(icon_x, icon_y), (icon_x + icon_box_size, icon_y + icon_box_size)],
        radius=20,
        fill=NAVY_SURFACE + (255,),
        outline=ORANGE_VIBRANT + (255,),
        width=2
    )
    draw_flat_logo(draw, cx=icon_x + icon_box_size // 2, cy=icon_y + icon_box_size // 2, size=75)

    # App title next to icon
    draw.text((icon_x + icon_box_size + 24, icon_y + 12), "VeloJot", font=font_title, fill=WHITE + (255,))
    
    # Tagline
    draw.text((icon_x, icon_y + 125), "Fast Offline Notes & Tasks", font=font_tagline, fill=ORANGE_VIBRANT + (255,))

    # Feature bullet points
    features = [
        "100% Offline-First Architecture (Zero Permissions)",
        "Fluid Spring Physics & Tactile Bouncing Feedback",
        "Interactive Checklists & Instant Search",
        "SQLite Room Persistence & Secure Local Storage"
    ]
    
    start_y = icon_y + 175
    for i, feat in enumerate(features):
        fy = start_y + i * 42
        # Bullet badge in orange
        draw.ellipse([(icon_x, fy + 4), (icon_x + 12, fy + 16)], fill=ORANGE_VIBRANT + (255,))
        draw.text((icon_x + 24, fy), feat, font=font_body, fill=SLATE_LIGHT + (255,))

    # Right Section: Flat Minimalist UI Card Mockups
    # Card 1: Pinned Quick Note
    card1_x1, card1_y1, card1_x2, card1_y2 = 620, 60, 960, 230
    draw.rounded_rectangle(
        [(card1_x1, card1_y1), (card1_x2, card1_y2)],
        radius=16,
        fill=(13, 31, 60, 255),
        outline=(30, 58, 102, 255),
        width=2
    )
    # Accent strip on top of Card 1
    draw.rounded_rectangle([(card1_x1, card1_y1), (card1_x2, card1_y1 + 4)], radius=2, fill=ORANGE_VIBRANT + (255,))
    
    # Pin badge
    draw.rounded_rectangle([(card1_x1 + 16, card1_y1 + 16), (card1_x1 + 86, card1_y1 + 38)], radius=6, fill=ORANGE_VIBRANT + (255,))
    draw.text((card1_x1 + 24, card1_y1 + 20), "PINNED", font=font_badge, fill=NAVY_DARK + (255,))

    draw.text((card1_x1 + 16, card1_y1 + 50), "Architecture Sprint Notes", font=ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 19), fill=WHITE + (255,))
    draw.text((card1_x1 + 16, card1_y1 + 80), "• Implemented Room DB with KSP\n• Configured StateFlow reactive pipeline\n• Tactile bouncy touch feedback", font=ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 15), fill=SLATE_LIGHT + (255,))

    # Card 2: Interactive Checklist
    card2_x1, card2_y1, card2_x2, card2_y2 = 580, 260, 930, 440
    draw.rounded_rectangle(
        [(card2_x1, card2_y1), (card2_x2, card2_y2)],
        radius=16,
        fill=(18, 40, 76, 255),
        outline=(45, 82, 140, 255),
        width=2
    )
    # Accent tag on Card 2
    draw.rounded_rectangle([(card2_x1 + 16, card2_y1 + 16), (card2_x1 + 110, card2_y1 + 38)], radius=6, fill=(16, 185, 129, 255))
    draw.text((card2_x1 + 24, card2_y1 + 20), "CHECKLIST", font=font_badge, fill=WHITE + (255,))

    draw.text((card2_x1 + 16, card2_y1 + 48), "Launch Readiness Review", font=ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 19), fill=WHITE + (255,))
    
    # Checklist item 1 (checked)
    draw.rounded_rectangle([(card2_x1 + 16, card2_y1 + 82), (card2_x1 + 32, card2_y1 + 98)], radius=4, fill=(16, 185, 129, 255))
    draw.text((card2_x1 + 40, card2_y1 + 80), "Zero unnecessary permissions verified", font=ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 15), fill=SLATE_MUTED + (255,))
    # Strike through
    draw.line([(card2_x1 + 40, card2_y1 + 90), (card2_x1 + 310, card2_y1 + 90)], fill=SLATE_MUTED + (255,), width=1)

    # Checklist item 2 (checked)
    draw.rounded_rectangle([(card2_x1 + 16, card2_y1 + 112), (card2_x1 + 32, card2_y1 + 128)], radius=4, fill=(16, 185, 129, 255))
    draw.text((card2_x1 + 40, card2_y1 + 110), "Fluid spring damping animations active", font=ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 15), fill=SLATE_MUTED + (255,))
    # Strike through
    draw.line([(card2_x1 + 40, card2_y1 + 120), (card2_x1 + 315, card2_y1 + 120)], fill=SLATE_MUTED + (255,), width=1)

    # Checklist item 3 (active)
    draw.rounded_rectangle([(card2_x1 + 16, card2_y1 + 142), (card2_x1 + 32, card2_y1 + 158)], radius=4, outline=ORANGE_VIBRANT + (255,), fill=(0,0,0,0), width=2)
    draw.text((card2_x1 + 40, card2_y1 + 140), "Assemble debug build & test pass", font=ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 15), fill=WHITE + (255,))

    img.save(output_path, "PNG")
    print(f"Generated 1024x500 Feature Graphic: {output_path} ({img.size})")


def generate_store_listing_copy(output_path):
    text_content = """================================================================================
VELOJOT — GOOGLE PLAY STORE METADATA & POLICY-COMPLIANT COPY
================================================================================

APP TITLE (Exact, 7/30 characters):
VeloJot

SHORT DESCRIPTION (Max 80 characters — Current: 74 characters):
Fast, fluid offline notes and interactive checklists with zero permissions.

================================================================================
FULL DESCRIPTION (Max 4000 characters — Policy compliant, no competitor terms):
================================================================================

Capture your thoughts, plan your day, and organize your tasks at the speed of thought with VeloJot — the high-performance, private, and offline-first notepad designed for Android.

BUILT FOR VELOCITY & COMFORT
VeloJot combines minimalist utility with fluid spring physics. Every tap, swipe, and scroll responds with tactile micro-interactions that make writing and organizing effortless. Whether you are drafting quick thoughts, preparing meeting notes, or checking off items on your daily to-do list, VeloJot delivers an uncompromisingly smooth experience.

100% PRIVATE & OFFLINE-FIRST
Your notes belong exclusively to you. VeloJot does not require network access, internet permissions, cloud accounts, or third-party tracking. Everything you write is encrypted and stored locally on your device in a secure SQLite Room database. No background data collection, no telemetry, no tracking, and zero ads.

KEY FEATURES:

• Rich Notes & Interactive Checklists: Seamlessly toggle between freeform structured notes and interactive checklists. Mark items done with strike-through animations, add new tasks instantly, and keep everyday checklists organized.

• Tactile Spring Physics: Experience custom-tuned spring motion and bouncing scale feedback on buttons, cards, filter chips, and the floating action button.

• Smart Search & Instant Filters: Quickly locate any note or checklist with instant real-time search. Filter your library dynamically by All Notes, Pinned items, Quick Tasks, or Archived documents.

• Pin to Top: Keep your most urgent tasks, ideas, and critical notes prominently pinned at the top of your workspace.

• Palette Color Tags: Categorize your thoughts with elegant, subtle pastel accent tags (Classic Navy, Coral Blossom, Sage Mint, Sky Blue, Sunset Amber, Soft Lavender, Rose Quartz, and Slate Grey).

• Robust Auto-Save & Validation: Never lose an idea. Notes are saved automatically as you write and upon back navigation. Empty notes are detected and cleanly discarded to keep your workspace decluttered.

• Quick Actions & Easy Sharing: Long-press any card to pin, re-color, archive, delete, or share note text directly using native Android sharing options.

• Undo Protection: Accidentally delete a note? A convenient Snackbar notification lets you restore it with a single tap.

• Pure Edge-to-Edge Design: Engineered strictly for modern Android with complete WindowInsets support, smooth keyboard adjustment, and balanced Dark and Light themes.

SPECIFICATIONS & PERMISSIONS:
• Internet Permission: NONE (Zero network access required)
• Storage Permissions: NONE (Uses private application sandbox)
• Analytics / Advertising SDKs: ZERO
• Minimum Android Version: Android 8.0 (API Level 26) or higher

Experience clean productivity without distractions. Download VeloJot today!
"""
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(text_content.strip())
    print(f"Generated Store Listing Copy: {output_path}")


def generate_launcher_mipmaps(res_dir):
    sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    for folder, sz in sizes.items():
        folder_path = os.path.join(res_dir, folder)
        os.makedirs(folder_path, exist_ok=True)
        img = Image.new("RGBA", (sz, sz), NAVY_DARK + (255,))
        draw = ImageDraw.Draw(img)
        draw.polygon([(0, 0), (sz, sz), (sz, 0)], fill=NAVY_ACCENT + (255,))
        draw_flat_logo(draw, cx=sz // 2, cy=sz // 2, size=int(sz * 0.65))

        out_file = os.path.join(folder_path, "ic_launcher.png")
        img.save(out_file, "PNG")

        # Round icon version
        mask = Image.new("L", (sz, sz), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.ellipse([(0, 0), (sz, sz)], fill=255)
        round_img = Image.new("RGBA", (sz, sz), (0, 0, 0, 0))
        round_img.paste(img, (0, 0), mask=mask)
        out_round = os.path.join(folder_path, "ic_launcher_round.png")
        round_img.save(out_round, "PNG")
        print(f"Generated {folder}: {out_file} and {out_round}")


if __name__ == "__main__":
    store_dir = r"D:\Antigravity\store listing\VeloJot"
    os.makedirs(store_dir, exist_ok=True)

    icon_path = os.path.join(store_dir, "app_icon_512x512.png")
    feature_path = os.path.join(store_dir, "feature_graphic_1024x500.png")
    copy_path = os.path.join(store_dir, "store_listing_copy.txt")

    generate_app_icon_512(icon_path)
    generate_feature_graphic_1024x500(feature_path)
    generate_store_listing_copy(copy_path)

    res_dir = r"D:\Antigravity\VeloJot\app\src\main\res"
    generate_launcher_mipmaps(res_dir)
    print("All graphic assets and metadata successfully generated!")
