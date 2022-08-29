export access_key_id="xm1Sc1R2UwnH5CpDgl0W"
export secret_key="epMx1bvLVOjbQPlBpCRsjHQXqjx9OLR9wFQA48Lj"
export domain_id="default"
export project_id="9d617d6f65dd4d6c861c1d785afa6aa0"

swift --os-auth-url https://kr.archive.ncloudstorage.com:5000/v3 --auth-version 3 \
      --os-username ${access_key_id} --os-password ${secret_key} \
      --os-user-domain-id ${domain_id} --os-project-id ${project_id} \
      auth

