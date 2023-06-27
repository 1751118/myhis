// 查询列表数据
const getAppointmentPage = (params) => {
  return $axios({
    url: '/appointment/page',
    method: 'get',
    params,
  })
}

//按Id获取药品信息
const getAppointmentById = (params) => {
  return $axios({
    url: '/appointment/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteAppointment = (ids) => {
  return $axios({
    url: '/appointment/delete',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editAppointment = (params) => {
  return $axios({
    url: '/appointment/edit',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addAppointment = (params) => {
  return $axios({
    url: '/appointment/add',
    method: 'post',
    data: { ...params }
  })
}
