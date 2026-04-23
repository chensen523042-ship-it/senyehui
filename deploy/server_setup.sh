#!/bin/bash
# ===================================
# 森野汇 SaaS — 服务器端一键部署脚本
# 在服务器上运行：
#   cd /opt/senyehui && bash server_setup.sh
# ===================================

set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

DEPLOY_DIR="/opt/senyehui"

echo -e "${CYAN}========== [1/4] 检查环境 ==========${NC}"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker 未安装！请在宝塔面板 → Docker 中安装 Docker 服务${NC}"
    exit 1
fi
echo -e "${GREEN}Docker ✓${NC} $(docker --version)"

# 检查 docker compose
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
elif command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    echo -e "${RED}docker compose 未安装！${NC}"
    exit 1
fi
echo -e "${GREEN}Docker Compose ✓${NC} $($COMPOSE_CMD version)"

echo -e "${CYAN}========== [2/4] 检查文件结构 ==========${NC}"

# 检查必要文件
files_ok=true
for f in docker-compose.yml Dockerfile syh-api/target/syh-api-1.0.0-SNAPSHOT.jar \
         docs/database/init.sql docs/database/pay_order.sql \
         syh-h5/dist/index.html syh-admin-ui/dist/index.html; do
    if [ -f "$DEPLOY_DIR/$f" ]; then
        echo -e "  ${GREEN}✓${NC} $f"
    else
        echo -e "  ${RED}✗ 缺少文件: $f${NC}"
        files_ok=false
    fi
done
if [ "$files_ok" = false ]; then
    echo -e "${RED}文件不完整，请检查上传的部署包${NC}"
    exit 1
fi

echo -e "${CYAN}========== [3/4] 构建并启动 Docker 容器 ==========${NC}"

cd "$DEPLOY_DIR"

# 如果已有运行的容器，先停止
if $COMPOSE_CMD ps -q 2>/dev/null | grep -q .; then
    echo -e "${YELLOW}检测到运行中的容器，正在停止...${NC}"
    $COMPOSE_CMD down
fi

# 构建并启动
$COMPOSE_CMD up -d --build

echo -e "${CYAN}========== [4/4] 验证部署 ==========${NC}"

# 等待服务启动
echo -e "${YELLOW}等待服务启动 (最多 60 秒)...${NC}"
for i in $(seq 1 60); do
    if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8081/api/system/tenant/list 2>/dev/null | grep -q "200\|401\|403"; then
        echo -e "\n${GREEN}后端 API 已就绪 ✓${NC}"
        break
    fi
    if [ $i -eq 60 ]; then
        echo -e "\n${YELLOW}后端启动较慢，请稍后手动检查${NC}"
    fi
    printf "."
    sleep 1
done

# 显示容器状态
echo ""
$COMPOSE_CMD ps

echo -e "
${GREEN}========================================${NC}
${GREEN}  ✅ 森野汇 SaaS 部署完成！${NC}
${GREEN}========================================${NC}

${CYAN}接口信息:${NC}
  后端 API:  http://127.0.0.1:8081

${CYAN}前端文件位置:${NC}
  H5 端:    $DEPLOY_DIR/syh-h5/dist/
  管理后台:  $DEPLOY_DIR/syh-admin-ui/dist/

${YELLOW}下一步操作（在宝塔面板中完成）:${NC}
  1. 网站 → 添加站点 → 域名填 118.25.19.216 → 纯静态
  2. 站点设置 → 配置文件 → 用 nginx_baota.conf 的内容替换
  3. 安全 → 放行端口 80
  4. 腾讯云安全组放行端口 80

${CYAN}访问地址（配置 Nginx 后可用）:${NC}
  H5 端:    http://118.25.19.216/
  管理后台:  http://118.25.19.216/admin/
"
