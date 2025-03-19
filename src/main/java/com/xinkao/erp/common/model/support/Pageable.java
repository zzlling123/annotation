package com.xinkao.erp.common.model.support;

import javax.validation.constraints.Min;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共分页对象
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pageable{
	/**
	 * 当前页
	 */
	@Min(value = 1, message = "当前页最小为1")
	private Integer page = 1;

	/**
	 * 当前条数
	 */
	@Min(value = 1, message = "分页条数最小为1")
	private Integer pageSize = 10;

    /**
     * 将pageable转为Page对象
     * @return
     */
    public Page toPage() {
        if (null == this.getPage()) {
            this.setPage(1);
        }
        if (null == this.getPageSize()) {
            this.setPageSize(10);
        }
        Page page = new Page<>(this.getPage(), this.getPageSize());
        return page;
    }
}
