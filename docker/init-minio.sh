#!/bin/bash
# MinIO 初始化脚本
# 创建用于存储头像的 bucket

MINIO_ENDPOINT="http://localhost:9000"
MINIO_ACCESS_KEY="minioadmin"
MINIO_SECRET_KEY="minioadmin123"
BUCKET_NAME="testflowai-avatars"

# 使用 mc (MinIO Client) 创建 bucket
# 如果没有安装 mc，可以使用 curl 调用 MinIO API

echo "正在初始化 MinIO..."
echo "创建 bucket: $BUCKET_NAME"

# 检查 mc 是否已安装
if command -v mc &> /dev/null; then
    mc alias set testflowai "$MINIO_ENDPOINT" "$MINIO_ACCESS_KEY" "$MINIO_SECRET_KEY"
    mc mb "testflowai/$BUCKET_NAME" --ignore-existing
    mc anonymous set download "testflowai/$BUCKET_NAME"
    echo "MinIO 初始化完成！"
else
    # 使用 curl 创建 bucket
    curl -X PUT "$MINIO_ENDPOINT/$BUCKET_NAME" \
        --user "$MINIO_ACCESS_KEY:$MINIO_SECRET_KEY"
    echo ""
    echo "MinIO bucket 已创建（如果尚未存在）"
fi
