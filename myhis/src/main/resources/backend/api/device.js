// 查询列表数据
const getDevicePage = (params) => {
  return $axios({
    url: '/device/page',
    method: 'get',
    params,
  })
}

//按Id获取药品信息
const getDeviceById = (params) => {
  return $axios({
    url: '/device/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteDevice = (ids) => {
  return $axios({
    url: '/device/delete',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editDevice = (params) => {
  return $axios({
    url: '/device/edit',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addDevice = (params) => {
  return $axios({
    url: '/device/add',
    method: 'post',
    data: { ...params }
  })
}
