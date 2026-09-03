# 📰 Personal Intelligence OS

### *The News App That Thinks With You*

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Made with Gemini](https://img.shields.io/badge/Made%20with-Gemini-8A2BE2)](https://deepmind.google/technologies/gemini/)
[![UI: Nothing OS 5.0](https://img.shields.io/badge/UI-Nothing%20OS%205.0-black)](https://nothing.tech/)
[![Platform: Web + Mobile](https://img.shields.io/badge/Platform-Web%20%26%20Mobile-4ADE80)]()

---

## 🧠 What Is This?

**Personal Intelligence OS** is a fully-featured, AI-powered news application that doesn't just show you the news—it helps you understand it, act on it, and grow from it.

Built for **myself** (but open-source for anyone), this app combines:

- **25+ intelligent features** powered by Google's Gemini AI
- **Nothing OS 5.0 design language** — frosted glass, adaptive colors, spring animations
- **Complete offline support** with local vector database
- **Cross-device sync** via your own cloud storage
- **Zero reliance on paid social APIs** — uses free scraping tools

---

## ✨ Features

### 🧠 Smart Reading
- **Conversational Search** — Ask follow-up questions with citations
- **Public Pulse** — Automated sentiment analysis from X & Reddit
- **Visual Data Decoder** — AI describes charts and infographics
- **Global Lens** — Compare international media coverage
- **Fact Cross-Check** — Detect contradictions across sources
- **Scenario Simulator** — "What if?" future predictions

### 🎯 Personal Intelligence
- **Second Brain Vault** — Auto-extract facts, quotes, and entities from saved articles
- **The Mirror** — Weekly cognitive wellness reports
- **Smart Folders** — Dynamic, AI-organized article collections
- **The Archive** — Year-in-review and reading heatmaps
- **Noise Cancellation** — Filter by keywords and sentiment
- **Mood Dial** — Deep Dive, Quick Scan, or Balanced modes
- **Complexity Dial** — Beginner, Intermediate, or Expert reading levels

### ⏰ Time & Action
- **Agenda Weaving** — Auto-add tasks to your calendar
- **Deadline Mode** — 1-minute to 30-minute reading compression
- **Anti-Surprise Pre-Brief** — Predict tomorrow's headlines
- **Offline Vault** — Nightly downloads for offline reading
- **The Daily Reset** — Morning/evening routines with Audio Briefing

### 👥 Social & Fun
- **Trust Circles** — Share articles with discussion fuel
- **Gamification** — Daily quizzes, bias detection, swipe sorting
- **Reading Streaks & XP** — Turn reading into a habit

### 🎨 Design
- **Nothing OS 5.0 UI** — Frosted glass, adaptive colors, Geist typography
- **Spring Animations** — Fluid, interruptible physics
- **Quiet Mode** — Distraction-free reading
- **Dark/Light/System themes** with adaptive accent colors

### 🔧 Infrastructure
- **Cross-device sync** — Via iCloud, Google Drive, or Dropbox
- **Offline-first** — Works without internet
- **Local vector database** — For fast, private search
- **BYO API keys** — Use your own Gemini, X, and Reddit credentials

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Frontend** | React / Next.js / React Native |
| **AI** | Google Gemini 1.5 Pro & Flash |
| **UI** | Nothing OS 5.0 (Custom Design System) |
| **Database** | SQLite + ChromaDB (Vector) |
| **Scraping** | twifork (X), reddipy (Reddit), Apify (optional) |
| **Sync** | iCloud / Google Drive / Dropbox APIs |
| **Audio** | Google Cloud Text-to-Speech |
| **State Management** | Zustand / Redux Toolkit |
| **Animations** | Framer Motion / react-spring / react-native-reanimated |

---

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ or Python 3.10+
- Gemini API key (free tier available)
- (Optional) X and Reddit API credentials

### Installation

```bash
