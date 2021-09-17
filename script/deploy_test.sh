# 杰华用户名
USER=wb.xiejiehua
# 杰华本机的rsa key
RSA_KEY="C:\Users\\${USER}\.ssh\id_rsa_workrsa"
# 测试服ip
HOST="10.202.37.5"
# 复制到目标路径下
DEST_DIR="/home/${USER}/a24_workspace/a24-core"
# 当前路径
CURRENT_DIR=$(pwd)
# 打包核心模块
artifacts_dir_path="C:\Users\wb.xiejiehua\IdeaProjects\a24_workspace_springboot\a24_core\target\a24_core-0.0.1-SNAPSHOT.jar"

# 联通测试服创建文件夹
ssh -p 32200 ${USER}@${HOST} -i ${RSA_KEY} "mkdir -p ${DEST_DIR} && rm -rf ${DEST_DIR}/*"
if [[ $? -ne 0 ]]; then
  error_log "ssh failed"
  exit 1
fi

# 备份
cp ${artifacts_dir_path} ${CURRENT_DIR}/backup/a24_core.jar
echo ${CURRENT_DIR}

# 传输文件到测试服上
scp -r -i ${RSA_KEY} -P 32200 ${CURRENT_DIR}/backup/a24_core.jar ${USER}@${HOST}:${DEST_DIR}
if [[ $? -ne 0 ]]; then
  error_log "scp failed"
  exit 1
fi
