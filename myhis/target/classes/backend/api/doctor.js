function getDoctorList (params) {
  return $axios({
    url: '/doctor/page',
    method: 'get',
    params
  })
}

// 修改---启用禁用接口
function enableOrDisableEmployee (params) {
  return $axios({
    url: '/doctor',
    method: 'put',
    data: { ...params }
  })
}

// 新增---添加医生
function addDoctor (params) {
  return $axios({
    url: '/doctor/add',
    method: 'post',
    data: { ...params }
  })
}

function getOriginalDoctor(params){
  return $axios({
    url: '/doctor/edit-get',
    method: 'get',
    params,
  })
}
// 修改---添加员工
function editDoctorFinish (params) {
  return $axios({
    url: '/doctor/edit-finish',
    method: 'put',
    data: { ...params }
  })
}

//删除一位医生
function deleteDoctor(params){
  return $axios({
    url: '/doctor/delete',
    method: 'delete',
    data: { ...params }
  })
}
