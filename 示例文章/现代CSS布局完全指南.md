# 现代 CSS 布局完全指南：Grid 与 Flexbox

CSS 布局技术经历了从 table 布局到 float 布局，再到现在的 Flexbox 和 Grid 布局的演进。本文将深入讲解这两种现代布局技术的使用方法和最佳实践。

## 1. Flexbox 布局

### 1.1 基本概念

Flexbox 是一维布局模型，主要用于在一个方向上（行或列）排列元素。

```css
.container {
  display: flex;
  /* 或 display: inline-flex; */
}
```

### 1.2 主轴与交叉轴

- **主轴 (Main Axis)**: flex-direction 定义的方向
- **交叉轴 (Cross Axis)**: 垂直于主轴的方向

```css
.container {
  flex-direction: row; /* row | row-reverse | column | column-reverse */
}
```

### 1.3 常用属性

#### 容器属性

```css
.container {
  /* 主轴对齐 */
  justify-content: center; /* flex-start | flex-end | center | space-between | space-around | space-evenly */
  
  /* 交叉轴对齐 */
  align-items: center; /* flex-start | flex-end | center | baseline | stretch */
  
  /* 换行 */
  flex-wrap: wrap; /* nowrap | wrap | wrap-reverse */
  
  /* 多行对齐 */
  align-content: space-between;
}
```

#### 项目属性

```css
.item {
  /* 放大比例 */
  flex-grow: 1;
  
  /* 缩小比例 */
  flex-shrink: 1;
  
  /* 基础大小 */
  flex-basis: 200px;
  
  /* 简写 */
  flex: 1 1 200px; /* flex-grow flex-shrink flex-basis */
  
  /* 单独对齐 */
  align-self: flex-end;
  
  /* 排序 */
  order: 2;
}
```

### 1.4 实战案例：导航栏

```html
<nav class="navbar">
  <div class="logo">Logo</div>
  <ul class="nav-links">
    <li><a href="#">Home</a></li>
    <li><a href="#">About</a></li>
    <li><a href="#">Services</a></li>
    <li><a href="#">Contact</a></li>
  </ul>
  <button class="cta">Sign Up</button>
</nav>
```

```css
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #333;
  color: white;
}

.nav-links {
  display: flex;
  gap: 2rem;
  list-style: none;
}

.nav-links a {
  color: white;
  text-decoration: none;
}
```

## 2. Grid 布局

### 2.1 基本概念

Grid 是二维布局系统，可以同时处理行和列。

```css
.container {
  display: grid;
  /* 或 display: inline-grid; */
}
```

### 2.2 定义网格

#### 显式网格

```css
.container {
  /* 定义列 */
  grid-template-columns: 200px 1fr 2fr;
  /* 或使用 repeat */
  grid-template-columns: repeat(3, 1fr);
  /* 或使用 minmax */
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  
  /* 定义行 */
  grid-template-rows: 100px auto 100px;
  
  /* 间距 */
  gap: 20px; /* row-gap column-gap 的简写 */
}
```

#### 网格区域

```css
.container {
  grid-template-areas:
    "header header header"
    "sidebar main main"
    "footer footer footer";
}

.header { grid-area: header; }
.sidebar { grid-area: sidebar; }
.main { grid-area: main; }
.footer { grid-area: footer; }
```

### 2.3 项目定位

```css
.item {
  /* 基于线定位 */
  grid-column: 1 / 3; /* 从第1条线到第3条线 */
  grid-row: 2 / 4;
  
  /* 或使用 span */
  grid-column: span 2; /* 跨越2列 */
  
  /* 简写 */
  grid-area: 2 / 1 / 4 / 3; /* row-start / column-start / row-end / column-end */
}
```

### 2.4 对齐方式

```css
.container {
  /* 项目在单元格内的对齐 */
  justify-items: center; /* start | end | center | stretch */
  align-items: center;
  place-items: center; /* align-items justify-items 的简写 */
  
  /* 整个网格在容器内的对齐 */
  justify-content: center; /* start | end | center | stretch | space-around | space-between | space-evenly */
  align-content: center;
  place-content: center;
}

.item {
  /* 单个项目的对齐 */
  justify-self: end;
  align-self: end;
  place-self: end;
}
```

### 2.5 实战案例：响应式卡片布局

```html
<div class="card-grid">
  <div class="card">Card 1</div>
  <div class="card">Card 2</div>
  <div class="card">Card 3</div>
  <div class="card">Card 4</div>
  <div class="card">Card 5</div>
  <div class="card">Card 6</div>
</div>
```

```css
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  padding: 2rem;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.card:hover {
  transform: translateY(-5px);
}
```

## 3. Flexbox vs Grid：如何选择？

### 3.1 使用 Flexbox 的场景

- 一维布局（单行或单列）
- 导航栏
- 按钮组
- 表单元素对齐
- 内容不确定的动态布局

### 3.2 使用 Grid 的场景

- 二维布局（同时控制行和列）
- 整体页面布局
- 卡片网格
- 复杂的对齐需求
- 固定的布局结构

### 3.3 组合使用

```css
/* Grid 用于整体布局 */
.page {
  display: grid;
  grid-template-areas:
    "header"
    "main"
    "footer";
  min-height: 100vh;
}

/* Flexbox 用于导航栏 */
.header {
  grid-area: header;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Grid 用于主内容区的卡片 */
.main {
  grid-area: main;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}
```

## 4. 实战项目：完整页面布局

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>现代布局示例</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: system-ui, -apple-system, sans-serif;
    }

    .page {
      display: grid;
      grid-template-areas:
        "header header"
        "sidebar main"
        "footer footer";
      grid-template-columns: 250px 1fr;
      grid-template-rows: auto 1fr auto;
      min-height: 100vh;
    }

    .header {
      grid-area: header;
      background: #333;
      color: white;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .nav {
      display: flex;
      gap: 2rem;
      list-style: none;
    }

    .sidebar {
      grid-area: sidebar;
      background: #f5f5f5;
      padding: 2rem;
    }

    .main {
      grid-area: main;
      padding: 2rem;
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 2rem;
      align-content: start;
    }

    .card {
      background: white;
      border-radius: 8px;
      padding: 2rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .footer {
      grid-area: footer;
      background: #333;
      color: white;
      padding: 2rem;
      text-align: center;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
      .page {
        grid-template-areas:
          "header"
          "main"
          "sidebar"
          "footer";
        grid-template-columns: 1fr;
      }

      .main {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <div class="page">
    <header class="header">
      <div class="logo">Logo</div>
      <nav>
        <ul class="nav">
          <li><a href="#">Home</a></li>
          <li><a href="#">About</a></li>
          <li><a href="#">Contact</a></li>
        </ul>
      </nav>
    </header>

    <aside class="sidebar">
      <h3>Sidebar</h3>
      <ul>
        <li>Link 1</li>
        <li>Link 2</li>
        <li>Link 3</li>
      </ul>
    </aside>

    <main class="main">
      <div class="card">Card 1</div>
      <div class="card">Card 2</div>
      <div class="card">Card 3</div>
      <div class="card">Card 4</div>
    </main>

    <footer class="footer">
      <p>&copy; 2024 Your Company</p>
    </footer>
  </div>
</body>
</html>
```

## 5. 常见布局模式

### 5.1 圣杯布局

```css
.holy-grail {
  display: grid;
  grid-template: auto 1fr auto / auto 1fr auto;
  min-height: 100vh;
}

.header { grid-column: 1 / 4; }
.left-sidebar { grid-column: 1 / 2; }
.main-content { grid-column: 2 / 3; }
.right-sidebar { grid-column: 3 / 4; }
.footer { grid-column: 1 / 4; }
```

### 5.2 等高列

```css
.equal-height {
  display: flex;
  gap: 1rem;
}

.column {
  flex: 1;
  /* 所有列自动等高 */
}
```

### 5.3 垂直居中

```css
/* Flexbox */
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

/* Grid */
.grid-center {
  display: grid;
  place-items: center;
  min-height: 100vh;
}
```

## 总结

Flexbox 和 Grid 是现代 CSS 布局的两大支柱：

- **Flexbox**: 适合一维布局，灵活且强大
- **Grid**: 适合二维布局，精确且可控

掌握这两种技术，你可以轻松应对各种复杂的布局需求。记住，它们不是互相替代的关系，而是互补的工具，可以组合使用以实现最佳效果。

---

**标签**: CSS, 布局, Flexbox, Grid, 前端开发
**分类**: 前端技术
**阅读时间**: 12 分钟
