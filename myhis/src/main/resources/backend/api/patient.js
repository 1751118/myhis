function getPatientList (params) {
  return $axios({
    url: '/patient/page',
    method: 'get',
    params
  })
}

// 修改---启用禁用接口
function enableOrDisableEmployee (params) {
  return $axios({
    url: '/patient',
    method: 'put',
    data: { ...params }
  })
}

// 新增---添加患者
function addPatient (params) {
  return $axios({
    url: '/patient/add',
    method: 'post',
    data: { ...params }
  })
}

function getOriginalPatient(params){
  return $axios({
    url: '/patient/edit-get',
    method: 'get',
    params,
  })
}
// 修改---添加员工
function editPatientFinish (params) {
  return $axios({
    url: '/patient/edit-finish',
    method: 'put',
    data: { ...params }
  })
}

//删除一位患者
function deletePatient(params){
  return $axios({
    url: '/patient/delete',
    method: 'delete',
    data: { ...params }
  })
}
