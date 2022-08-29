const SwiftClient = require("openstack-swift-client");
const fs = require('fs');
const uploadPath = "/home/dblab/ysh/pipe_processing/sm/data/";

let credentials = {
  endpointUrl: "https://kr.archive.ncloudstorage.com:5000/v3",
  username: "xm1Sc1R2UwnH5CpDgl0W",
  password: "epMx1bvLVOjbQPlBpCRsjHQXqjx9OLR9wFQA48Lj",
  domainId: "default",
  projectId: "9d617d6f65dd4d6c861c1d785afa6aa0",
};

// swift client
const client = new SwiftClient(
  new SwiftClient.KeystoneV3Authenticator(credentials)
);
const container_name = "tracking-ut";
const container = client.container(container_name);

const ping = (filename, datecode, uuid, timecode, filetype) => {
  return new Promise(async (resolve) => {
    const object_name = `${datecode}/${uuid}/${timecode}.${filetype}`;
    const local_file_name = filename;
    const extra_header = {
      "Content-Type": "text/plain",
    };
    try{
      await container.create(
        object_name,
        fs.createReadStream(local_file_name), // 한개의 데이터만 테스트 해보기, 로컬로 만들어서 해보기
        null,
        extra_header
      );
    }catch(error){
      console.log("error")
      console.log(object_name)
      console.log(error)
    }
    resolve();
  });
};
function sleep(ms) {
  const wakeUpTime = Date.now() + ms;
  while (Date.now() < wakeUpTime) {}
}

async function run() {
  var count = 0
  fs.readdir('/home/dblab/ysh/pipe_processing/sm/data', (err, filelist) => {
    filelist.forEach(async file => {
      var filename = file.split('_');

      if (count % 1000 == 0){
        console.log(count)
        console.log(filename[0]+'_'+filename[1]+'_'+filename[2])
        //sleep(30000)
      }
      count = count + 1

      await ping(
        uploadPath + filename[0]+'_'+filename[1]+'_'+filename[2],
        filename[0],
        filename[1],
        filename[2].slice(0,-4),
        "csv"
      );
    })
  });
}
run();
