// 查询列表数据
const getPrescriptionPage = (params) => {
  return $axios({
    url: '/prescription/page',
    method: 'get',
    params
  })
}

//按Id获取处方信息
const getPrescriptionById = (params) => {
  return $axios({
    url: '/prescription/get',
    method: 'get',
    params
  })
}

// 删除数据接口
const deletePrescription = (ids) => {
  return $axios({
    url: '/prescription/delete',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editPrescription = (params) => {
  return $axios({
    url: '/prescription/edit',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addPrescription = (params) => {
  return $axios({
    url: '/prescription/add',
    method: 'post',
    data: { ...params }
  })
}

// 批量起售禁售
const prescriptionStatusByStatus = (params) => {
  return $axios({
    url: `/prescription/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
