package com.finalist.newsletter.services;

import java.util.List;
import java.util.Map;

import com.finalist.newsletter.domain.StatisticResult;

public interface StatisticService {

	public List<StatisticResult> statisticAll ();

	public List<StatisticResult> statisticByNewsletter (int newsletterId);

	public List<StatisticResult> statisticAllByPeriod (String start, String end)
			throws ServiceException;

	public StatisticResult statisticByNewsletterPeriod (int newsletterId,
			String start, String end) throws ServiceException;

	public StatisticResult statisticSummery ();

	public StatisticResult statisticSummeryPeriod (String start, String end)
			throws ServiceException;

	public StatisticResult StatisticSummaryByNewsletter (int newsletterId);

	public List<StatisticResult> StatisticDetailByNewsletterPeriod (
			int newsletterId, String start, String end) throws ServiceException;
}
