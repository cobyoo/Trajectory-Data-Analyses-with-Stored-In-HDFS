path='/home/dblab/ysh/pipe_processing/sm/data'

for filepath in $path/*
do
  filename=$(echo ${filepath##/*/} | sed -e 's/\_/\//g')
  swift --os-storage-url https://kr.archive.ncloudstorage.com/v1/AUTH_9d617d6f65dd4d6c861c1d785afa6aa0 \
	--os-auth-token gAAAAABhOhYA7zDLKaoRJQzo9NmAwoFfCnB_BPgKCyzzyXJc8tXSkKxSoQvKvEvdsZtytpoSqfmY3z8MtcCbycwc-nryjGbYBUbxbCmFqEbE8kEBFzwQqX6Z6AlxXqW3n3y_2HRTy68dR4VI9httKXixcWCnp3mBrZJpgpBJhQfmNLrfDq9xR5Y \
	list tracking-ut
done
